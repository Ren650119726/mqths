package com.reefe.mqths.core.service.impl;

import com.reefe.mqths.common.bean.context.MqthTransactionContext;
import com.reefe.mqths.core.handler.MqthTransactionHandler;
import com.reefe.mqths.core.handler.MqthTransactionHandlerFactory;
import com.reefe.mqths.core.helper.SpringBeanUtils;
import com.reefe.mqths.core.service.MqthTransactionAspectService;
import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 事务切面 service
 * @Auther: REEFE
 * @Date: 2018/6/6/006
 */
@Service
public class MqthTransactionAspectServiceImpl implements MqthTransactionAspectService {

    /**
     * mqth事务切面服务
     *
     * @param mqthTransactionContext myth事务上下文对象
     * @param point                  切点
     * @return object
     * @throws Throwable 异常信息
     */
    @Override
    public Object invoke(MqthTransactionContext mqthTransactionContext, ProceedingJoinPoint point) throws Throwable {
        //根据事务状态 返回事务处理类 Class
        final Class clazz = MqthTransactionHandlerFactory.factoryOf(mqthTransactionContext);
        MqthTransactionHandler handler = (MqthTransactionHandler)SpringBeanUtils.getInstance().getBean(clazz);
        return handler.handler(point, mqthTransactionContext);
    }
}
