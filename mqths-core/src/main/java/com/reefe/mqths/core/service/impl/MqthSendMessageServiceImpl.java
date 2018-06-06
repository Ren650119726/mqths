package com.reefe.mqths.core.service.impl;

import com.reefe.mqths.common.bean.entity.MqthParticipant;
import com.reefe.mqths.common.bean.entity.MqthTransaction;
import com.reefe.mqths.common.bean.mq.MessageEntity;
import com.reefe.mqths.common.serializer.ObjectSerializer;
import com.reefe.mqths.core.disruptor.MqthTransactionEventPublisher;
import com.reefe.mqths.core.helper.SpringBeanUtils;
import com.reefe.mqths.core.service.MqthMqSendService;
import com.reefe.mqths.core.service.MqthSendMessageService;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

/**
 * @Auther: REEFE
 * @Date: 2018/6/6/006
 */
@Service("mqthSendMessageService")
public class MqthSendMessageServiceImpl implements MqthSendMessageService {
    private Logger logger = LoggerFactory.getLogger(MqthSendMessageServiceImpl.class);

    private volatile ObjectSerializer serializer;

    private volatile MqthMqSendService mqthMqSendService;

    @Autowired
    private MqthTransactionEventPublisher publisher;

    /**
     * 发送消息
     *
     * @param mqthTransaction 消息体
     * @return true 处理成功  false 处理失败
     */
    @Override
    public Boolean sendMessage(MqthTransaction mqthTransaction) {
        if (Objects.isNull(mqthTransaction)) {
            return false;
        } else {
            List<MqthParticipant> mqthParticipants = mqthTransaction.getMqthParticipants();
            /*
             * 这里的这个判断很重要，不为空，表示本地的方法执行成功，需要执行远端的rpc方法
             * 为什么呢，因为我会在切面的finally里面发送消息，意思是切面无论如何都需要发送mq消息
             * 那么考虑问题，如果本地执行成功，调用rpc的时候才需要发
             * 如果本地异常，则不需要发送mq ，此时mqthParticipants为空
             */
            if (CollectionUtils.isNotEmpty(mqthParticipants)) {
                for (MqthParticipant mqthParticipant : mqthParticipants) {
                    MessageEntity messageEntity = new MessageEntity();
                    messageEntity.setTransId(mqthTransaction.getTransId());
                    messageEntity.setMqthInvocation(mqthParticipant.getMqthInvocation());

                    try {
                        final byte[] message = getObjectSerializer().serialize(messageEntity);
                        //指定发送的队列名 还有消息模式(queue或topic)
                        getMqthMqSendService().sendMessage(mqthParticipant.getDestination(),
                                mqthParticipant.getPattern(),
                                message);
                    } catch (Exception e) {
                        e.printStackTrace();
                        return Boolean.FALSE;
                    }

                }
            }
        }
        return false;
    }

    private synchronized MqthMqSendService getMqthMqSendService() {
        if (mqthMqSendService == null) {
            synchronized (MqthSendMessageServiceImpl.class) {
                if (mqthMqSendService == null) {
                    mqthMqSendService = SpringBeanUtils.getInstance().getBean(MqthMqSendService.class);
                }
            }
        }
        return mqthMqSendService;
    }

    private synchronized ObjectSerializer getObjectSerializer() {
        if (serializer == null) {
            synchronized (MqthSendMessageServiceImpl.class) {
                if (serializer == null) {
                    serializer = SpringBeanUtils.getInstance().getBean(ObjectSerializer.class);
                }
            }
        }
        return serializer;
    }

}
