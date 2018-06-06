package com.reefe.mqths.rocketmq;

import com.google.common.base.Splitter;
import com.reefe.mqths.common.constant.CommonConstant;
import com.reefe.mqths.common.exception.MqthRuntimeException;
import com.reefe.mqths.core.service.MqthMqSendService;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Rocketmq 发生消息服务
 *
 * @Auther: REEFE
 * @Date: 2018/6/6/006
 */
public class RocketmqSendServiceImpl implements MqthMqSendService {
    private static final Logger LOGGER = LoggerFactory.getLogger(RocketmqSendServiceImpl.class);

    private DefaultMQProducer defaultMQProducer;

    public void setDefaultMQProducer(DefaultMQProducer defaultMQProducer) {
        this.defaultMQProducer = defaultMQProducer;
    }

    /**
     * 发送消息
     *
     * @param destination 队列
     * @param pattern     mq 模式
     * @param message     MythTransaction实体对象转换成byte[]后的数据
     */
    @Override
    public Boolean sendMessage(String destination, Integer pattern, byte[] message) {
        try {
            Message msg;
            List<String> stringList =
                    Splitter.on(CommonConstant.TOPIC_TAG_SEPARATOR).trimResults().splitToList(destination);
            if (stringList.size() > 1) {
                String topic = stringList.get(0);
                String tags = stringList.get(1);
                msg = new Message(topic, tags, message);
            } else {
                msg = new Message(destination, "*", message);
            }
            final SendResult sendResult = defaultMQProducer.send(msg);
            LOGGER.debug(sendResult.toString());
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.error(e.getMessage());
            throw new MqthRuntimeException();
        }
    }
}
