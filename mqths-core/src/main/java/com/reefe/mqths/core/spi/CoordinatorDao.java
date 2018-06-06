package com.reefe.mqths.core.spi;

import com.reefe.mqths.common.bean.entity.MqthTransaction;
import com.reefe.mqths.common.config.MqthConfig;
import com.reefe.mqths.common.exception.MqthRuntimeException;
import com.reefe.mqths.common.serializer.ObjectSerializer;

import java.util.Date;
import java.util.List;

/**
 * @Auther: REEFE
 * @Date: 2018/6/5/005
 */
public interface CoordinatorDao {


    /**
     * 初始化操作
     *
     * @param modelName  模块名称
     * @param mqthConfig 配置信息
     * @throws MqthRuntimeException 自定义异常
     */
    void init(String modelName, MqthConfig mqthConfig) throws MqthRuntimeException;

    /**
     * 设置scheme
     *
     * @return scheme 命名
     */
    String getScheme();


    /**
     * 设置序列化信息
     *
     * @param objectSerializer 序列化实现
     */
    void setSerializer(ObjectSerializer objectSerializer);


    /**
     * 创建本地事务对象
     *
     * @param mqthTransaction 事务对象
     * @return rows 1 成功   0 失败
     */
    int create(MqthTransaction mqthTransaction);

    /**
     * 更新补偿数据状态
     *
     * @param transId 事务id
     * @param status  状态
     * @return rows 1 成功
     * @throws MqthRuntimeException 异常
     */
    int updateStatus(String transId, int status) throws MqthRuntimeException;

    /**
     * 更新事务失败日志
     *
     * @param mqthTransaction 实体对象
     * @return rows 1 成功
     * @throws MqthRuntimeException 异常信息
     */
    int updateParticipant(MqthTransaction mqthTransaction) throws MqthRuntimeException;

    /**
     * 更新事务失败日志
     *
     * @param mqthTransaction 实体对象
     * @return rows 1 成功
     * @throws MqthRuntimeException 异常信息
     */
    int updateFailTransaction(MqthTransaction mqthTransaction) throws MqthRuntimeException;

    /**
     * 获取延迟多长时间后的事务信息,只要为了防止并发的时候，刚新增的数据被执行
     *
     * @param date 延迟后的时间
     * @return List<MythTransaction>
     */
    List<MqthTransaction> listAllByDelay(Date date);
}
