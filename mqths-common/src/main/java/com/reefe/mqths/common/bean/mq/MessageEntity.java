package com.reefe.mqths.common.bean.mq;

import com.reefe.mqths.common.bean.entity.MqthInvocation;

/**
 * @Auther: REEFE
 * @Date: 2018/6/6/006
 */
public class MessageEntity {
    /**
     * 事务id
     */
    private String transId;

    /**
     * 执行器
     */
    private MqthInvocation mqthInvocation;

    public String getTransId() {
        return transId;
    }

    public void setTransId(String transId) {
        this.transId = transId;
    }

    public MqthInvocation getMqthInvocation() {
        return mqthInvocation;
    }

    public void setMqthInvocation(MqthInvocation mqthInvocation) {
        this.mqthInvocation = mqthInvocation;
    }
}
