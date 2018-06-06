package com.reefe.mqths.core.interceptor;

import org.aspectj.lang.ProceedingJoinPoint;

/**
 * 事务拦截器接口  主要实现在rpc 实现
 * @Auther: REEFE
 * @Date: 2018/6/5/005
 */
@FunctionalInterface //函数式接口，只能有且仅有一个方法
public interface MqthTransactionInterceptor {
    /**
     * 分布式事务拦截方法
     *
     * @param pjp tcc切入点
     * @return Object
     * @throws Throwable 异常
     */
    Object interceptor(ProceedingJoinPoint pjp) throws Throwable;
}
