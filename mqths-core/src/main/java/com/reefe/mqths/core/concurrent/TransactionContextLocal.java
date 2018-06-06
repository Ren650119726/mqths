package com.reefe.mqths.core.concurrent;

import com.reefe.mqths.common.bean.context.MqthTransactionContext;

/**
 * 利用ThreadLocal用于保存线程内事务上下文
 *
 * @Auther: REEFE
 * @Date: 2018/6/6/006
 */
public class TransactionContextLocal {
    private static final TransactionContextLocal transactionContextLocal = new TransactionContextLocal();
    private static final ThreadLocal<MqthTransactionContext> CURRENT_LOCAL = new ThreadLocal<>();

    private TransactionContextLocal() {
    }

    ;


    public static TransactionContextLocal getInstance() {
        return transactionContextLocal;
    }

    public MqthTransactionContext get() {
        return CURRENT_LOCAL.get();
    }

    public void set(MqthTransactionContext context) {
        CURRENT_LOCAL.set(context);
    }

    public void remove() {
        CURRENT_LOCAL.remove();
    }

}
