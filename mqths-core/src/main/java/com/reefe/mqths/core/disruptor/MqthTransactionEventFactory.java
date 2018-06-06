package com.reefe.mqths.core.disruptor;

import com.lmax.disruptor.EventFactory;

/**
 * disruptor 事件工厂
 * @Auther: REEFE
 * @Date: 2018/6/5/005
 */
public class MqthTransactionEventFactory implements EventFactory<MqthTransactionEvent> {

    @Override
    public MqthTransactionEvent newInstance() {
        return new MqthTransactionEvent();
    }
}
