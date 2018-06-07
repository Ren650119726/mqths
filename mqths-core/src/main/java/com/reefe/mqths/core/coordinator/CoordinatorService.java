package com.reefe.mqths.core.coordinator;

import com.reefe.mqths.common.bean.entity.MqthTransaction;
import com.reefe.mqths.common.config.MqthConfig;
import com.reefe.mqths.common.exception.MqthException;

import java.util.Date;
import java.util.List;

/**
 * 协调者
 *
 * @Auther: REEFE
 * @Date: 2018/6/5/005
 */
public interface CoordinatorService {
    /**
     * 开始本地事务 只要是创建数据库本地消息事务表
     *
     * @param mqthConfig 配置信息
     * @throws MqthException 异常
     */
    void start(MqthConfig mqthConfig) throws MqthException;


    /**
     * 保存本地事务
     *
     * @param mqthTransaction
     */
    String save(MqthTransaction mqthTransaction);

    /**
     * 更新参与者
     *
     * @param mqthTransaction
     */
    int updateParticipant(MqthTransaction mqthTransaction);

    /**
     * 更新事务状态
     *
     * @param transId
     * @param status
     */
    void updateStatus(String transId, int status);

    /**
     * 更新事务为失败状态
     *
     * @param mqthTransaction
     */
    void updateFailTransaction(MqthTransaction mqthTransaction);

    List<MqthTransaction> listAllByDelay(Date date);

    /**
     * 根据事务查询事务
     *
     * @param transId
     * @return
     */
    MqthTransaction findByTransId(String transId);
}
