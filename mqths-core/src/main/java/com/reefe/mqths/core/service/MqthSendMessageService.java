package com.reefe.mqths.core.service;

import com.reefe.mqths.common.bean.entity.MqthTransaction;

/**
 * @Auther: REEFE
 * @Date: 2018/6/6/006
 */
public interface MqthSendMessageService {
    /**
     * 发送消息
     *
     * @param mqthTransaction 消息体
     * @return true 处理成功  false 处理失败
     */
    Boolean sendMessage(MqthTransaction mqthTransaction);
}
