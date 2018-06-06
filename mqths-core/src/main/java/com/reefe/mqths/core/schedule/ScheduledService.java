package com.reefe.mqths.core.schedule;

import com.reefe.mqths.common.bean.entity.MqthTransaction;
import com.reefe.mqths.common.config.MqthConfig;
import com.reefe.mqths.common.enums.EventTypeEnum;
import com.reefe.mqths.common.enums.MqthStatusEnum;
import com.reefe.mqths.common.utils.DateUtils;
import com.reefe.mqths.core.coordinator.CoordinatorService;
import com.reefe.mqths.core.disruptor.MqthTransactionEventPublisher;
import com.reefe.mqths.core.service.MqthSendMessageService;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


/**
 * 定时任务
 *
 * @author REEFE
 */
@Component
public class ScheduledService {

    /**
     * logger
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(ScheduledService.class);


    @Autowired
    private MqthSendMessageService mythSendMessageService;

    @Autowired
    private CoordinatorService coordinatorService;

    @Autowired
    private MqthTransactionEventPublisher publisher;

    /**
     * 开启任务调度 发送消息 发送成功并更改消息状态
     *
     * @param mqthConfig
     */
    public void scheduledAutoRecover(MqthConfig mqthConfig) {
        new ScheduledThreadPoolExecutor(1,
                MqthTransactionThreadFactory.create("MqthAutoRecoverService",
                        true)).scheduleWithFixedDelay(() -> {
            LOGGER.debug("auto recover execute delayTime:{}",
                    mqthConfig.getScheduledDelay());
            try {
                Date date = acquireData(mqthConfig);
                LOGGER.debug("任务调度延迟时间:" + DateUtils.parseDate(date));
                final List<MqthTransaction> mqthTransactionList =
                        coordinatorService.listAllByDelay(date);
                LOGGER.debug("任务调度列表：" + (mqthTransactionList != null ? mqthTransactionList.size() : ""));
                if (CollectionUtils.isNotEmpty(mqthTransactionList)) {
                    mqthTransactionList
                            .forEach(mqthTransaction -> {
                                final Boolean success = mythSendMessageService.sendMessage(mqthTransaction);
                                //发送成功 ，更改状态
                                if (success) {
                                    mqthTransaction.setStatus(MqthStatusEnum.COMMIT.getCode());
                                    publisher.publishEvent(mqthTransaction, EventTypeEnum.UPDATE_STATUS.getCode());
                                }
                            });
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }, 30, mqthConfig.getScheduledDelay(), TimeUnit.SECONDS);

    }


    private Date acquireData(MqthConfig mythConfig) {
        return new Date(LocalDateTime.now().atZone(ZoneId.systemDefault())
                .toInstant().toEpochMilli() - (mythConfig.getRecoverDelayTime() * 1000));
    }

}
