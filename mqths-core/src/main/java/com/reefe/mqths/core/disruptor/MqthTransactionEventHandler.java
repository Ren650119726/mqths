package com.reefe.mqths.core.disruptor;

import com.lmax.disruptor.EventHandler;
import com.reefe.mqths.common.bean.entity.MqthTransaction;
import com.reefe.mqths.common.enums.EventTypeEnum;
import com.reefe.mqths.core.coordinator.CoordinatorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 异步事务处理器
 * @Auther: REEFE
 * @Date: 2018/6/5/005
 */
@Component
public class MqthTransactionEventHandler implements EventHandler<MqthTransactionEvent> {

    @Autowired
    private CoordinatorService coordinatorService;

    @Override
    public void onEvent(MqthTransactionEvent mqthTransactionEvent, long l, boolean b) throws Exception {
        int type = mqthTransactionEvent.getType();
        if(type == EventTypeEnum.SAVE.getCode()){
            coordinatorService.save(mqthTransactionEvent.getMqthTransaction());
        }else if(type == EventTypeEnum.UPDATE_PARTICIPANT.getCode()){
            coordinatorService.updateParticipant(mqthTransactionEvent.getMqthTransaction());
        }else if(type == EventTypeEnum.UPDATE_STATUS.getCode()){
            final MqthTransaction mqthTransaction = mqthTransactionEvent.getMqthTransaction();
            coordinatorService.updateStatus(mqthTransaction.getTransId(), mqthTransaction.getStatus());
        }else if(type == EventTypeEnum.UPDATE_FAIR.getCode()){
            coordinatorService.updateFailTransaction(mqthTransactionEvent.getMqthTransaction());
        }
    }
}
