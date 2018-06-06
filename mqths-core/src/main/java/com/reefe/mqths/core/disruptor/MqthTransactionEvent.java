package com.reefe.mqths.core.disruptor;

import com.reefe.mqths.common.bean.entity.MqthTransaction;

/**
 * disruptor 异步 事务事件
 * @Auther: REEFE
 * @Date: 2018/6/5/005
 */
public class MqthTransactionEvent {

    //事务信息表 对应数据库实体
    private MqthTransaction mqthTransaction;

    private int type;

    public void clear() {
        mqthTransaction = null;
    }

    public MqthTransaction getMqthTransaction() {
        return mqthTransaction;
    }

    public void setMqthTransaction(MqthTransaction mqthTransaction) {
        this.mqthTransaction = mqthTransaction;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
