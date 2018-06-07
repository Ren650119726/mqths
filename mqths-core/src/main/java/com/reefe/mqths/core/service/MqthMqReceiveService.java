package com.reefe.mqths.core.service;

/**
 * 接受MQ消息
 *
 * @Auther: REEFE
 * @Date: 2018/6/7/007
 */
public interface MqthMqReceiveService {


    /**
     * mqth框架处理发出的mq消息
     *
     * @param message 实体对象转换成byte[]后的数据
     * @return true 成功 false 失败
     */
    Boolean processMessage(byte[] message);
}
