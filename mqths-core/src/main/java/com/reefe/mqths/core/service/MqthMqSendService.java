package com.reefe.mqths.core.service;

/**
 * @Auther: REEFE
 * @Date: 2018/6/6/006
 */
@FunctionalInterface
public interface MqthMqSendService {
    /**
     * 发送消息
     *
     * @param destination 队列
     * @param pattern     mq 模式
     * @param message     MythTransaction实体对象转换成byte[]后的数据
     */
    void sendMessage(String destination, Integer pattern, byte[] message);

}
