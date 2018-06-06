package com.reefe.mqths.core.handler.impl;

import com.reefe.mqths.common.bean.context.MqthTransactionContext;
import com.reefe.mqths.core.handler.MqthTransactionHandler;
import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.stereotype.Service;

/**
 * 本地事务处理器
 * @Auther: REEFE
 * @Date: 2018/6/6/006
 */
@Service
public class LocalMqthTransactionHandler implements MqthTransactionHandler {

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
        //执行业务代码
        return point.proceed();
    }
}
