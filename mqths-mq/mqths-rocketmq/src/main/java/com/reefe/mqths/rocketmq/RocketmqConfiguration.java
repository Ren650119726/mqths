package com.reefe.mqths.rocketmq;

import com.reefe.mqths.core.service.MqthMqReceiveService;
import com.reefe.mqths.core.service.MqthMqSendService;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.apache.rocketmq.common.message.MessageExt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import java.util.List;

@Configuration
@ConditionalOnProperty(prefix = "spring.rocketmq", name = "namesrvAddr")
public class RocketmqConfiguration {


    private static final Logger LOGGER = LoggerFactory.getLogger(RocketmqConfiguration.class);

    private static final String TOPIC = "account";

    @Autowired
    private Environment env;

    @Autowired
    private MqthMqReceiveService mqthMqReceiveService;

    @SuppressWarnings("Duplicates")

    @ConditionalOnProperty(prefix = "spring.rocketmq", name = "role", havingValue = "consumer", matchIfMissing = true)
    @Bean
    public DefaultMQPushConsumer pushConsumer() throws MQClientException {
        /**
         * 一个应用创建一个Consumer，由应用来维护此对象，可以设置为全局对象或者单例<br>
         * 注意：ConsumerGroupName需要由应用来保证唯一
         */
        DefaultMQPushConsumer consumer = new
                DefaultMQPushConsumer(env.getProperty("spring.rocketmq.consumerGroupName"));
        consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_FIRST_OFFSET);
        consumer.setNamesrvAddr(env.getProperty("spring.rocketmq.namesrvAddr"));
        consumer.setInstanceName(env.getProperty("spring.rocketmq.instanceName"));
        //设置批量消费，以提升消费吞吐量，默认是1
        consumer.setConsumeMessageBatchMaxSize(1);

        /**
         * 订阅指定topic下tags
         */
        consumer.subscribe(TOPIC, "*");

        consumer.registerMessageListener((List<MessageExt> msgList, ConsumeConcurrentlyContext context) -> {

            MessageExt msg = msgList.get(0);
            try {
                // 默认msgList里只有一条消息，可以通过设置consumeMessageBatchMaxSize参数来批量接收消息
                final byte[] message = msg.getBody();
                LOGGER.debug(" rocketmq 接收到mqth框架发出的信息====");
                final Boolean success = mqthMqReceiveService.processMessage(message);
                if (success) {
                    return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
                }

            } catch (Exception e) {
                e.printStackTrace();
                //重复消费
                return ConsumeConcurrentlyStatus.RECONSUME_LATER;
            }

            //如果没有return success，consumer会重复消费此信息，直到success。
            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
        });

        /**
         * Consumer对象在使用之前必须要调用start初始化，初始化一次即可<br>
         */
        consumer.start();
        LOGGER.debug("RocketMQ register consumer success");
        return consumer;
    }

    @ConditionalOnProperty(prefix = "spring.rocketmq", name = "role", havingValue = "producer", matchIfMissing = true)
    @Bean
    public MqthMqSendService mqthMqSendService() throws MQClientException {
        RocketmqSendServiceImpl mqthMqSendService = new RocketmqSendServiceImpl();
        mqthMqSendService.setDefaultMQProducer(defaultMQProducer());
        return mqthMqSendService;
    }

    @ConditionalOnProperty(prefix = "spring.rocketmq", name = "role", havingValue = "producer", matchIfMissing = true)
    @Bean
    public DefaultMQProducer defaultMQProducer() throws MQClientException {
        /**
         * 一个应用创建一个Producer，由应用来维护此对象，可以设置为全局对象或者单例<br>
         * 注意：ProducerGroupName需要由应用来保证唯一<br>
         * ProducerGroup这个概念发送普通的消息时，作用不大，但是发送分布式事务消息时，比较关键，
         * 因为服务器会回查这个Group下的任意一个Producer
         */
        DefaultMQProducer producer = new DefaultMQProducer(env.getProperty("spring.rocketmq.producerGroupName"));
        producer.setNamesrvAddr(env.getProperty("spring.rocketmq.namesrvAddr"));
        producer.setRetryTimesWhenSendFailed(env.getProperty("spring.rocketmq.retryTimesWhenSendFailed", Integer.class));
        producer.setVipChannelEnabled(false);
        producer.setRetryTimesWhenSendAsyncFailed(10);

        /**
         * Producer对象在使用之前必须要调用start初始化，初始化一次即可<br>
         * 注意：切记不可以在每次发送消息时，都调用start方法
         */
        producer.start();
        LOGGER.debug("RocketMQ register producer success");
        return producer;

    }
}
