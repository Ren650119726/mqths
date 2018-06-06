package com.reefe.mqths.common.bean.entity;

import com.google.common.collect.Lists;
import com.reefe.mqths.common.utils.IdWorkerUtils;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 事务信息表 对应数据库实体
 *
 * @Auther: REEFE
 * @Date: 2018/6/5/005
 */
public class MqthTransaction implements Serializable {

    private static final long serialVersionUID = -8851334884972921319L;

    /**
     * 事务id
     */
    private String transId;

    /**
     * 事务状态 {@linkplain com.reefe.mqths.common.enums.MqthStatusEnum}
     */
    private int status;

    /**
     * 事务类型(角色) {@linkplain com.reefe.mqths.common.enums.MqthRoleEnum}
     */
    private int role;

    /**
     * 重试次数
     */
    private volatile int retriedCount = 0;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date lastTime;

    /**
     * 版本号 乐观锁控制 控制接口幂等性
     */
    private Integer version = 1;

    /**
     * 调用接口名称
     */
    private String targetClass;

    /**
     * 调用方法名称
     */
    private String targetMethod;

    /**
     * 调用错误信息
     */
    private String errorMsg;

    /**
     * 参与协调方法集合
     */
    private List<MqthParticipant> mqthParticipants;

    public MqthTransaction() {
        this.transId = IdWorkerUtils.getInstance().createUUID();
        this.createTime = new Date();
        this.lastTime = new Date();
        mqthParticipants = Lists.newCopyOnWriteArrayList();

    }

    public MqthTransaction(String transId) {
        this.transId = transId;
        this.createTime = new Date();
        this.lastTime = new Date();
        mqthParticipants = Lists.newCopyOnWriteArrayList();
    }


    public String getTransId() {
        return transId;
    }

    public void setTransId(String transId) {
        this.transId = transId;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getRole() {
        return role;
    }

    public void setRole(int role) {
        this.role = role;
    }

    public int getRetriedCount() {
        return retriedCount;
    }

    public void setRetriedCount(int retriedCount) {
        this.retriedCount = retriedCount;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getLastTime() {
        return lastTime;
    }

    public void setLastTime(Date lastTime) {
        this.lastTime = lastTime;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public String getTargetClass() {
        return targetClass;
    }

    public void setTargetClass(String targetClass) {
        this.targetClass = targetClass;
    }

    public String getTargetMethod() {
        return targetMethod;
    }

    public void setTargetMethod(String targetMethod) {
        this.targetMethod = targetMethod;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public List<MqthParticipant> getMqthParticipants() {
        return mqthParticipants;
    }

    public void setMqthParticipants(List<MqthParticipant> mqthParticipants) {
        this.mqthParticipants = mqthParticipants;
    }

    public void registerParticipant(MqthParticipant participant) {
        mqthParticipants.add(participant);
    }
}
