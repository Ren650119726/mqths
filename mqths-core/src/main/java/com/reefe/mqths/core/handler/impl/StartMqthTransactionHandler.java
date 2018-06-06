package com.reefe.mqths.core.handler.impl;

import com.reefe.mqths.common.bean.context.MqthTransactionContext;
import com.reefe.mqths.core.concurrent.TransactionContextLocal;
import com.reefe.mqths.core.handler.MqthTransactionHandler;
import com.reefe.mqths.core.manager.MqthTransactionManager;
import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 发起者处理器
 *
 * @Auther: REEFE
 * @Date: 2018/6/6/006
 */
@Service
public class StartMqthTransactionHandler implements MqthTransactionHandler {

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
            //保存事务 开始状态 发起者角色
            mqthTransactionManager.begin(point);
            return point.proceed();

        } catch (Throwable throwable) {
            // 失败 记录日志
            mqthTransactionManager.fail(throwable.getMessage());
            throw throwable;
        } finally {
            //发送MQ消息
            mqthTransactionManager.sendMessage();
            mqthTransactionManager.cleanThreadLocal();
            TransactionContextLocal.getInstance().remove();
        }
    }
}
