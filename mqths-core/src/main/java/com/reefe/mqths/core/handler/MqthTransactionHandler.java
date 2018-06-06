package com.reefe.mqths.core.handler;

import com.reefe.mqths.common.bean.context.MqthTransactionContext;
import org.aspectj.lang.ProceedingJoinPoint; /**
 * @Auther: REEFE
 * @Date: 2018/6/6/006
 */
public interface MqthTransactionHandler {
    /**
     * Mqth分布式事务处理接口
     *
     * @param point                  point 切点
     * @param mqthTransactionContext myth事务上下文
     * @return Object
     * @throws Throwable 异常
     */
    Object handler(ProceedingJoinPoint point, MqthTransactionContext mqthTransactionContext) throws Throwable;
}
