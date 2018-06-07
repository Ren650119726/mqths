package com.reefe.mqths.rabbitmq;

import com.reefe.mqths.core.service.MqthMqReceiveService;
import com.reefe.mqths.core.service.MqthMqSendService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.ChannelAwareMessageListener;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

/**
 * @Auther: REEFE
 * @Date: 2018/6/7/007
 */
@Component
@Configuration
public class RabbitmqConfiguration {

    private static final String EXCHANGE = "account";
    private static final String ROUTING_KEY = "account";

    private static final Logger LOGGER = LoggerFactory.getLogger(RabbitmqConfiguration.class);

    @Autowired
    private MqthMqReceiveService mqthMqReceiveService;

    @Autowired
    private ConnectionFactory connectionFactory;

    @Autowired
    private AmqpTemplate amqpTemplate;

    /**
     * 针对消费者配置
     * 1. 设置交换机类型
     * 2. 将队列绑定到交换机
     * <p>
     * <p>
     * FanoutExchange: 将消息分发到所有的绑定队列，无routingkey的概念
     * HeadersExchange ：通过添加属性key-value匹配
     * DirectExchange:按照routingkey分发到指定队列
     * TopicExchange:多关键字匹配
     */
    @Bean
    public DirectExchange defaultExchange() {
        return new DirectExchange(EXCHANGE);
    }

    @Bean
    public Queue queue() {
        //队列持久
        return new Queue("account", true);

    }

    @Bean
    public Binding binding() {
        return BindingBuilder.bind(queue()).to(defaultExchange()).with(RabbitmqConfiguration.ROUTING_KEY);
    }


    @Bean
    @ConditionalOnProperty(prefix = "spring.rabbitmq", name = "role", havingValue = "consumer", matchIfMissing = true)
    public SimpleMessageListenerContainer messageContainer() {
        SimpleMessageListenerContainer container =
                new SimpleMessageListenerContainer(connectionFactory);
        container.setQueues(queue());
        container.setExposeListenerChannel(true);
        container.setMaxConcurrentConsumers(3);
        container.setConcurrentConsumers(1);
        //设置确认模式手工确认
        container.setAcknowledgeMode(AcknowledgeMode.MANUAL);
        container.setMessageListener((ChannelAwareMessageListener) (message, channel) -> {
            byte[] messageBody = message.getBody();
            LOGGER.debug("amqp接收消息");
            //确认消息成功消费
            final Boolean success = mqthMqReceiveService.processMessage(messageBody);
            if (success) {
                channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
            }
        });
        return container;
    }

    @ConditionalOnProperty(prefix = "spring.rabbitmq", name = "role", havingValue = "producer", matchIfMissing = true)
    @Bean
    public MqthMqSendService mqthMqSendService() {
        RabbitmqSendServiceImpl rabbitmqSendService = new RabbitmqSendServiceImpl();
        rabbitmqSendService.setAmqpTemplate(amqpTemplate);
        LOGGER.debug("RabbitMQ.amqpTemplate register to rabbitmqSendService success");
        return rabbitmqSendService;
    }

}
