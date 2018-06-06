package com.reefe.mqths.core.manager;

import com.reefe.mqths.common.bean.context.MqthTransactionContext;
import com.reefe.mqths.common.bean.entity.MqthParticipant;
import com.reefe.mqths.common.bean.entity.MqthTransaction;
import com.reefe.mqths.common.enums.EventTypeEnum;
import com.reefe.mqths.common.enums.MqthRoleEnum;
import com.reefe.mqths.common.enums.MqthStatusEnum;
import com.reefe.mqths.core.concurrent.TransactionContextLocal;
import com.reefe.mqths.core.disruptor.MqthTransactionEventPublisher;
import com.reefe.mqths.core.service.MqthSendMessageService;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Objects;
import java.util.Optional;

/**
 * 事务管理者
 *
 * @Auther: REEFE
 * @Date: 2018/6/6/006
 */
@Component
public class MqthTransactionManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(MqthTransactionManager.class);
    /**
     * 将事务信息存放在threadLocal里面
     */
    private static final ThreadLocal<MqthTransaction> CURRENT = new ThreadLocal<>();

    @Autowired
    private MqthTransactionEventPublisher publisher;

    @Autowired
    private MqthSendMessageService mqthSendMessageService;

    /**
     * 开始
     *
     * @param point
     */
    public MqthTransaction begin(ProceedingJoinPoint point) {
        LOGGER.debug("开始执行Mqth分布式事务！start");
        MqthTransaction mqthTransaction =
                buildMqthTransaction(point, MqthRoleEnum.START.getCode(),
                        MqthStatusEnum.BEGIN.getCode(), "");

        //发布事务保存事件，Disrupotr 异步保存mythTransaction实体到数据库
        publisher.publishEvent(mqthTransaction, EventTypeEnum.SAVE.getCode());

        //当前事务保存到ThreadLocal
        CURRENT.set(mqthTransaction);

        //创建事务上下文对象
        MqthTransactionContext mqthTransactionContext = new MqthTransactionContext();
        //设置事务id
        mqthTransactionContext.setTransId(mqthTransaction.getTransId());
        //设置为发起者角色
        mqthTransactionContext.setRole(MqthRoleEnum.START.getCode());
        //设置 MqthTransactionContext 事务上下文对象，这个类会传递给远端
        TransactionContextLocal.getInstance().set(mqthTransactionContext);
        return mqthTransaction;
    }

    /**
     * 失败
     *
     * @param message
     */
    public void fail(String message) {
        MqthTransaction mqthTransaction = getCurrentTransaction();
        if (Objects.nonNull(mqthTransaction)) {
            mqthTransaction.setStatus(MqthStatusEnum.FAILURE.getCode());
            mqthTransaction.setErrorMsg(message);
            //发布 更改事务失败状态 事件
            publisher.publishEvent(mqthTransaction, EventTypeEnum.UPDATE_FAIR.getCode());
        }
    }


    private MqthTransaction buildMqthTransaction(ProceedingJoinPoint point, int role, int status,
                                                 String transId) {
        MqthTransaction mqthTransaction;
        if (StringUtils.isNoneBlank(transId)) {
            mqthTransaction = new MqthTransaction(transId);
        } else {
            mqthTransaction = new MqthTransaction();
        }

        MethodSignature signature = (MethodSignature) point.getSignature();
        Method method = signature.getMethod();
        Class<?> clazz = point.getTarget().getClass();
        mqthTransaction.setStatus(status);
        mqthTransaction.setRole(role);
        mqthTransaction.setTargetClass(clazz.getName());
        mqthTransaction.setTargetMethod(method.getName());
        return mqthTransaction;
    }

    public boolean isBegin() {
        return CURRENT.get() != null;
    }

    public void cleanThreadLocal() {
        CURRENT.remove();
    }

    private MqthTransaction getCurrentTransaction() {
        return CURRENT.get();
    }

    /**
     * 参与者参与
     *
     * @param point
     * @param mqthTransactionContext
     * @return
     */
    public MqthTransaction actorTransaction(ProceedingJoinPoint point, MqthTransactionContext mqthTransactionContext) {
        MqthTransaction mqthTransaction =
                buildMqthTransaction(point, MqthRoleEnum.PROVIDER.getCode(),
                        MqthStatusEnum.BEGIN.getCode(), mqthTransactionContext.getTransId());

        //发布事务保存事件，异步保存
        publisher.publishEvent(mqthTransaction, EventTypeEnum.SAVE.getCode());

        //当前事务保存到ThreadLocal
        CURRENT.set(mqthTransaction);

        //设置提供者角色
        mqthTransactionContext.setRole(MqthRoleEnum.PROVIDER.getCode());

        TransactionContextLocal.getInstance().set(mqthTransactionContext);

        return mqthTransaction;
    }

    /**
     * 更改事务状态
     *
     * @param status 事务状态码
     */
    public void updateStatus(int status) {
        MqthTransaction mqthTransaction = getCurrentTransaction();
        Optional.ofNullable(mqthTransaction)
                .map(t -> {
                    t.setStatus(status);
                    return t;
                }).ifPresent(t -> publisher.publishEvent(t, EventTypeEnum.UPDATE_STATUS.getCode()));
        //设置已经提交状态
        mqthTransaction.setStatus(MqthStatusEnum.COMMIT.getCode());
    }

    public void registerParticipant(MqthParticipant participant) {
        final MqthTransaction mqthTransaction = this.getCurrentTransaction();
        mqthTransaction.registerParticipant(participant);
        publisher.publishEvent(mqthTransaction, EventTypeEnum.UPDATE_PARTICIPANT.getCode());
    }

    /**
     * 发送MQ消息
     */
    public void sendMessage() {
        MqthTransaction mythTransaction = getCurrentTransaction();
        if (Objects.nonNull(mythTransaction)) {
            mqthSendMessageService.sendMessage(mythTransaction);
        }
    }
}
