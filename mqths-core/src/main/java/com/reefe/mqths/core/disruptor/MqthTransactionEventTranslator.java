package com.reefe.mqths.core.disruptor;

import com.lmax.disruptor.EventTranslatorOneArg;
import com.reefe.mqths.common.bean.entity.MqthTransaction;

/**
 *
 * @Auther: REEFE
 * @Date: 2018/6/5/005
 */
public class MqthTransactionEventTranslator implements EventTranslatorOneArg<MqthTransactionEvent, MqthTransaction> {

    private int type;

    public MqthTransactionEventTranslator(int type) {
        this.type = type;
    }

    @Override
    public void translateTo(MqthTransactionEvent mqthTransactionEvent, long l,
                            MqthTransaction mqthTransaction) {
        mqthTransactionEvent.setMqthTransaction(mqthTransaction);
        mqthTransactionEvent.setType(type);
    }
}
