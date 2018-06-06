package com.reefe.mqths.core.service;

import com.reefe.mqths.common.bean.context.MqthTransactionContext;
import org.aspectj.lang.ProceedingJoinPoint;

/**
 * @Auther: REEFE
 * @Date: 2018/6/6/006
 */
@FunctionalInterface
public interface MqthTransactionAspectService {
    /**
     * myth事务切面服务
     *
     * @param mqthTransactionContext myth事务上下文对象
     * @param point                 切点
     * @return object
     * @throws Throwable 异常信息
     */
    Object invoke(MqthTransactionContext mqthTransactionContext, ProceedingJoinPoint point) throws Throwable;
}
