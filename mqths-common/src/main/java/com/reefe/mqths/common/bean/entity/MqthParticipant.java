package com.reefe.mqths.common.bean.entity;

import java.io.Serializable;

/**
 * 参与者
 * @Auther: REEFE
 * @Date: 2018/6/5/005
 */
public class MqthParticipant implements Serializable{
    private static final long serialVersionUID = 1994469643836317484L;
    /**
     * 事务id
     */
    private String transId;

    /**
     * 队列 名称(TOPIC,如果是rocketmq或者aliyunmq,这里包含TOPIC和TAG)用,区分
     */
    private String destination;

    /**
     * 消息模式
     */
    private Integer pattern;

    public MqthParticipant() {
    }

    public MqthParticipant(String transId, String destination, Integer pattern, MqthInvocation mqthInvocation) {
        this.transId = transId;
        this.destination = destination;
        this.pattern = pattern;
        this.mqthInvocation = mqthInvocation;
    }

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

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public Integer getPattern() {
        return pattern;
    }

    public void setPattern(Integer pattern) {
        this.pattern = pattern;
    }

    public MqthInvocation getMqthInvocation() {
        return mqthInvocation;
    }

    public void setMqthInvocation(MqthInvocation mqthInvocation) {
        this.mqthInvocation = mqthInvocation;
    }
}
