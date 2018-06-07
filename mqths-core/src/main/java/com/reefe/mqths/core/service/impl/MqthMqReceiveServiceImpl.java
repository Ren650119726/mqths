package com.reefe.mqths.core.service.impl;

import com.reefe.mqths.common.bean.context.MqthTransactionContext;
import com.reefe.mqths.common.bean.entity.MqthInvocation;
import com.reefe.mqths.common.bean.entity.MqthTransaction;
import com.reefe.mqths.common.bean.mq.MessageEntity;
import com.reefe.mqths.common.config.MqthConfig;
import com.reefe.mqths.common.enums.EventTypeEnum;
import com.reefe.mqths.common.enums.MqthRoleEnum;
import com.reefe.mqths.common.enums.MqthStatusEnum;
import com.reefe.mqths.common.exception.MqthException;
import com.reefe.mqths.common.exception.MqthRuntimeException;
import com.reefe.mqths.common.serializer.ObjectSerializer;
import com.reefe.mqths.core.concurrent.TransactionContextLocal;
import com.reefe.mqths.core.coordinator.CoordinatorService;
import com.reefe.mqths.core.disruptor.MqthTransactionEventPublisher;
import com.reefe.mqths.core.helper.SpringBeanUtils;
import com.reefe.mqths.core.service.MqthMqReceiveService;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @Auther: REEFE
 * @Date: 2018/6/7/007
 */
@Service("mqthMqReceiveService")
public class MqthMqReceiveServiceImpl implements MqthMqReceiveService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MqthMqReceiveServiceImpl.class);

    private volatile ObjectSerializer serializer;

    private static final Lock LOCK = new ReentrantLock();

    @Autowired
    private CoordinatorService coordinatorService;

    @Autowired
    private MqthTransactionEventPublisher publisher;

    @Autowired
    private MqthConfig mqthConfig;

    /**
     * mqth框架处理发出的mq消息
     *
     * @param message 实体对象转换成byte[]后的数据
     * @return true 成功 false 失败
     */
    @Override
    public Boolean processMessage(byte[] message) {
        try {
            MessageEntity entity;
            try {
                entity = getObjectSerializer().deSerialize(message, MessageEntity.class);
            } catch (MqthException e) {
                e.printStackTrace();
                throw new MqthRuntimeException(e.getMessage());
            }
            /*
             * 1 检查该事务有没被处理过，已经处理过的 则不处理
             * 2 发起发射调用，调用接口，进行处理
             * 3 记录本地日志
             */
            LOCK.lock();

            final String transId = entity.getTransId();
            final MqthTransaction mythTransaction = coordinatorService.findByTransId(transId);

            //第一次调用 也就是服务down机，或者没有调用到的时候， 通过mq执行
            if (Objects.isNull(mythTransaction)) {
                try {
                    //反射执行业务方法
                    execute(entity);
                    //执行成功 保存成功的日志
                    final MqthTransaction log = buildTransactionLog(transId, "",
                            MqthStatusEnum.COMMIT.getCode(),
                            entity.getMqthInvocation().getTargetClass().getName(),
                            entity.getMqthInvocation().getMethodName());
                    //submit(new CoordinatorAction(CoordinatorActionEnum.SAVE, log));
                    publisher.publishEvent(log, EventTypeEnum.SAVE.getCode());
                } catch (Exception e) {
                    //执行失败保存失败的日志
                    final MqthTransaction log = buildTransactionLog(transId, e.getMessage(),
                            MqthStatusEnum.FAILURE.getCode(),
                            entity.getMqthInvocation().getTargetClass().getName(),
                            entity.getMqthInvocation().getMethodName());
                    publisher.publishEvent(log, EventTypeEnum.SAVE.getCode());
                    throw new MqthRuntimeException(e);
                } finally {
                    TransactionContextLocal.getInstance().remove();
                }

            } else {
                //如果是执行失败的话
                if (mythTransaction.getStatus() == MqthStatusEnum.FAILURE.getCode()) {
                    //如果超过了最大重试次数 则不执行
                    if (mythTransaction.getRetriedCount() >= mqthConfig.getRetryMax()) {
                        LOGGER.error("此事务已经超过了最大重试次数:" + mqthConfig.getRetryMax()
                                + " ,执行接口为:" + entity.getMqthInvocation().getTargetClass() + " ,方法为:" +
                                entity.getMqthInvocation().getMethodName() + ",事务id为：" + entity.getTransId());
                        return Boolean.FALSE;
                    }
                    try {
                        execute(entity);
                        //执行成功 更新日志为成功
                        mythTransaction.setStatus(MqthStatusEnum.COMMIT.getCode());
                        publisher.publishEvent(mythTransaction, EventTypeEnum.UPDATE_STATUS.getCode());

                    } catch (Throwable e) {
                        //执行失败，设置失败原因和重试次数
                        mythTransaction.setErrorMsg(e.getCause().getMessage());
                        mythTransaction.setRetriedCount(mythTransaction.getRetriedCount() + 1);
                        publisher.publishEvent(mythTransaction, EventTypeEnum.UPDATE_FAIR.getCode());
                        throw new MqthRuntimeException(e);
                    } finally {
                        TransactionContextLocal.getInstance().remove();
                    }
                }
            }

        } finally {
            LOCK.unlock();
        }
        return Boolean.TRUE;
    }

    private void execute(MessageEntity entity) throws Exception {
        //创建事务上下文，这个类会传递给远端
        MqthTransactionContext context = new MqthTransactionContext();
        //设置事务id
        context.setTransId(entity.getTransId());
        //设置为本地执行者角色
        context.setRole(MqthRoleEnum.LOCAL.getCode());
        TransactionContextLocal.getInstance().set(context);
        executeLocalTransaction(entity.getMqthInvocation());
    }

    private void executeLocalTransaction(MqthInvocation mqthInvocation) throws Exception {
        if (Objects.nonNull(mqthInvocation)) {
            final Class clazz = mqthInvocation.getTargetClass();
            final String method = mqthInvocation.getMethodName();
            final Class[] parameterTypes = mqthInvocation.getParameterTypes();
            final Object[] args = mqthInvocation.getArgs();
            final Object bean = SpringBeanUtils.getInstance().getBean(clazz);
            MethodUtils.invokeMethod(bean, method, args, parameterTypes);
            LOGGER.debug("Mqth执行本地协调事务:{}", mqthInvocation.getTargetClass()
                    + ":" + mqthInvocation.getMethodName());
        }
    }

    private MqthTransaction buildTransactionLog(String transId, String errorMsg, Integer status, String targetClass, String targetMethod) {
        MqthTransaction logTransaction = new MqthTransaction(transId);
        logTransaction.setRetriedCount(1);
        logTransaction.setStatus(status);
        logTransaction.setErrorMsg(errorMsg);
        logTransaction.setRole(MqthRoleEnum.PROVIDER.getCode());
        logTransaction.setTargetClass(targetClass);
        logTransaction.setTargetMethod(targetMethod);
        return logTransaction;
    }

    private synchronized ObjectSerializer getObjectSerializer() {
        if (serializer == null) {
            synchronized (MqthMqReceiveServiceImpl.class) {
                if (serializer == null) {
                    serializer = SpringBeanUtils.getInstance().getBean(ObjectSerializer.class);
                }
            }
        }
        return serializer;
    }
}
