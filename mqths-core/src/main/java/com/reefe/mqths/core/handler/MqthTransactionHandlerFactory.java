package com.reefe.mqths.core.handler;

import com.reefe.mqths.common.bean.context.MqthTransactionContext;
import com.reefe.mqths.common.enums.MqthRoleEnum;
import com.reefe.mqths.core.handler.impl.ActorMqthTransactionHandler;
import com.reefe.mqths.core.handler.impl.LocalMqthTransactionHandler;
import com.reefe.mqths.core.handler.impl.StartMqthTransactionHandler;
import com.reefe.mqths.core.manager.MqthTransactionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Objects;

/**
 * 事务处理器工厂
 * @Auther: REEFE
 * @Date: 2018/6/6/006
 */
@Component
public class MqthTransactionHandlerFactory{

    @Autowired
    private MqthTransactionManager mqthTransactionManager;

    private static MqthTransactionManager manager;

    @PostConstruct
    public void init() {
        manager = this.mqthTransactionManager;
    }


    /**
     * 返回 实现TxTransactionHandler类的名称
     *
     * @param context 事务上下文
     * @return Class
     * @throws Throwable 抛出异常
     */

    public static Class factoryOf(MqthTransactionContext context) throws Throwable {
        //如果事务还没开启或者 myth事务上下文是空， 那么应该进入发起调用
        if (!manager.isBegin() && Objects.isNull(context)) {
            return StartMqthTransactionHandler.class;
        } else {
            if (context.getRole() == MqthRoleEnum.LOCAL.getCode()) {
                return LocalMqthTransactionHandler.class;
            }
            return ActorMqthTransactionHandler.class;
        }
    }
}
