package com.reefe.mqths.core.handler.impl;

import com.reefe.mqths.common.bean.context.MqthTransactionContext;
import com.reefe.mqths.common.enums.MqthStatusEnum;
import com.reefe.mqths.core.concurrent.TransactionContextLocal;
import com.reefe.mqths.core.handler.MqthTransactionHandler;
import com.reefe.mqths.core.manager.MqthTransactionManager;
import org.aspectj.lang.ProceedingJoinPoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 参与者事务处理器
 *
 * @Auther: REEFE
 * @Date: 2018/6/6/006
 */
@Service
public class ActorMqthTransactionHandler implements MqthTransactionHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(ActorMqthTransactionHandler.class);

    @Autowired
    private MqthTransactionManager mqthTransactionManager;

    /**
     * Mqth分布式事务处理接口
     *
     * @param point                  point 切点
     * @param mqthTransactionContext myth事务上下文
     * @return Object
     * @throws Throwable 异常
     */
    @Override
    public Object handler(ProceedingJoinPoint point, MqthTransactionContext mqthTransactionContext) throws Throwable {
        try {
            //先保存事务 开始状态 提供者角色
            mqthTransactionManager.actorTransaction(point, mqthTransactionContext);

            //发起调用业务代码 执行try方法
            final Object proceed = point.proceed();

            //执行成功 事务更新状态为commit
            mqthTransactionManager.updateStatus(MqthStatusEnum.COMMIT.getCode());

            return proceed;

        } catch (Throwable throwable) {
            LOGGER.error("执行分布式事务接口失败,事务id：{}", mqthTransactionContext.getTransId());
            mqthTransactionManager.fail(throwable.getMessage());
            throw throwable;
        } finally {
            TransactionContextLocal.getInstance().remove();
        }
    }
}
