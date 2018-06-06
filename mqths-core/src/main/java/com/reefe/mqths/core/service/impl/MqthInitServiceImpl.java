package com.reefe.mqths.core.service.impl;

import com.reefe.mqths.common.config.MqthConfig;
import com.reefe.mqths.common.enums.RepositorySupportEnum;
import com.reefe.mqths.common.enums.SerializeEnum;
import com.reefe.mqths.common.exception.MqthException;
import com.reefe.mqths.common.serializer.ObjectSerializer;
import com.reefe.mqths.common.serializer.impl.KryoSerializer;
import com.reefe.mqths.core.coordinator.CoordinatorService;
import com.reefe.mqths.core.disruptor.MqthTransactionEventPublisher;
import com.reefe.mqths.core.helper.SpringBeanUtils;
import com.reefe.mqths.core.schedule.ScheduledService;
import com.reefe.mqths.core.service.MqthInitService;
import com.reefe.mqths.core.spi.CoordinatorDao;
import com.reefe.mqths.core.spi.impl.JdbcCoordinatorDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.ServiceLoader;
import java.util.stream.StreamSupport;

/**
 * @Auther: REEFE
 * @Date: 2018/6/4/004
 */
@Service
public class MqthInitServiceImpl implements MqthInitService {

    private static final Logger logger = LoggerFactory.getLogger(MqthInitServiceImpl.class);
    @Autowired
    private CoordinatorService coordinatorService;
    @Autowired
    private MqthTransactionEventPublisher publisher;
    @Autowired
    private ScheduledService scheduledService;

    /**
     * Mqth分布式事务初始化方法
     *
     * @param mqthConfig TCC配置
     */
    @Override
    public void initialization(MqthConfig mqthConfig) {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> logger.error("系统关闭")));
        try {
            loadSpiSupport(mqthConfig);
            publisher.start(mqthConfig.getBufferSize());
            coordinatorService.start(mqthConfig);
            //如果需要自动恢复 开启线程 调度线程池，进行恢复
            if (mqthConfig.getNeedRecover()) {
                scheduledService.scheduledAutoRecover(mqthConfig);
            }
        } catch (MqthException e) {
            logger.error("Mqth事务初始化异常:{}", e.getMessage());
            //非正常关闭
            System.exit(1);
        }
        logger.info("Mqth事务初始化成功！");
    }

    /**
     * 根据配置文件初始化spi 并加载到spring容器中
     *
     * @param mqthConfig
     */
    private void loadSpiSupport(MqthConfig mqthConfig) {
        // 序列化
        //spi  serialize enum
        final SerializeEnum serializeEnum =
                SerializeEnum.acquire(mqthConfig.getSerializer());
        //spi ObjectSerializer ServiceLoader
        final ServiceLoader<ObjectSerializer> objectSerializers = ServiceLoader.load(ObjectSerializer.class);
        //spi ObjectSerializer
        final ObjectSerializer serializer =
                StreamSupport.stream(objectSerializers.spliterator(),
                        true)
                        .filter(objectSerializer ->
                                Objects.equals(objectSerializer.getScheme(),
                                        serializeEnum.getSerialize()))
                        .findFirst()
                        .orElse(new KryoSerializer());

        SpringBeanUtils.getInstance().registerBean(ObjectSerializer.class.getName(), serializer);
        // 持久化
        //spi repository support
        final RepositorySupportEnum repositorySupportEnum =
                RepositorySupportEnum.acquire(mqthConfig.getRepositorySupport());
        final ServiceLoader<CoordinatorDao> coordinatorDaos = ServiceLoader.load(CoordinatorDao.class);
        final CoordinatorDao coordinatorDao = StreamSupport.stream(coordinatorDaos.spliterator(),
                true)
                .filter(coordinator ->
                        Objects.equals(coordinator.getScheme(),
                                repositorySupportEnum.getRepository()))
                .findFirst()
                .orElse(new JdbcCoordinatorDao());
        //将coordinatorDao实现注入到spring容器
        coordinatorDao.setSerializer(serializer);
        SpringBeanUtils.getInstance().registerBean(CoordinatorDao.class.getName(), coordinatorDao);
    }
}
