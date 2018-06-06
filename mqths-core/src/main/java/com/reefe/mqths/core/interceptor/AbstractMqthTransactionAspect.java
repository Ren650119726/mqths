package com.reefe.mqths.core.interceptor;

import com.reefe.mqths.common.bean.entity.MqthTransaction;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

/**
 * 本地事务切面
 * @Auther: REEFE
 * @Date: 2018/6/5/005
 */
@Aspect
public abstract class AbstractMqthTransactionAspect {
    private MqthTransactionInterceptor mqthTransactionInterceptor;

    public void setMqthTransactionInterceptor(MqthTransactionInterceptor mqthTransactionInterceptor) {
        this.mqthTransactionInterceptor = mqthTransactionInterceptor;
    }

    @Pointcut("@annotation(com.reefe.mqths.common.annotation.Mqth)")
    public void mqthTransactionInterceptor() {
    }

    @Around("mqthTransactionInterceptor()")
    public Object interceptCompensableMethod(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        return mqthTransactionInterceptor.interceptor(proceedingJoinPoint);
    }

    /**
     * spring Order 接口，该值的返回直接会影响springBean的加载顺序
     *
     * @return int 类型
     */
    public abstract int getOrder();
}
