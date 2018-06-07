package com.reefe.mqths.core.coordinator.impl;

import com.reefe.mqths.common.bean.entity.MqthTransaction;
import com.reefe.mqths.common.config.MqthConfig;
import com.reefe.mqths.common.exception.MqthException;
import com.reefe.mqths.core.coordinator.CoordinatorService;
import com.reefe.mqths.core.helper.SpringBeanUtils;
import com.reefe.mqths.core.service.ApplicationService;
import com.reefe.mqths.core.spi.CoordinatorDao;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * @Auther: REEFE
 * @Date: 2018/6/5/005
 */
@Service("coordinatorService")
public class CoordinatorServiceImpl implements CoordinatorService {
    private final static Logger logger = LoggerFactory.getLogger(CoordinatorServiceImpl.class);
    private CoordinatorDao coordinatorDao;
    private final ApplicationService applicationService;

    @Autowired
    public CoordinatorServiceImpl(ApplicationService applicationService) {
        this.applicationService = applicationService;
    }

    /**
     * 开始本地事务 只要是创建数据库本地消息事务表
     *
     * @param mqthConfig 配置信息
     * @throws MqthException 异常
     */
    @Override
    public void start(MqthConfig mqthConfig) throws MqthException {
        coordinatorDao = SpringBeanUtils.getInstance().getBean(CoordinatorDao.class);
        // 获取项目模块名称
        final String repositorySuffix = buildRepositorySuffix(mqthConfig.getRepositorySuffix());

        logger.info("系统" + repositorySuffix + "启动");

        //初始化spi 协调资源存储 并创建数据库表
        coordinatorDao.init(repositorySuffix, mqthConfig);
    }

    /**
     * 保存本地事务
     *
     * @param mqthTransaction
     */
    @Override
    public String save(MqthTransaction mqthTransaction) {
        final int rows = coordinatorDao.create(mqthTransaction);
        if (rows > 0) {
            return mqthTransaction.getTransId();
        }
        return null;
    }

    /**
     * 更新参与者
     *
     * @param mqthTransaction
     */
    @Override
    public int updateParticipant(MqthTransaction mqthTransaction) {
        return coordinatorDao.updateParticipant(mqthTransaction);
    }

    /**
     * 更新事务状态
     *
     * @param transId
     * @param status
     */
    @Override
    public void updateStatus(String transId, int status) {
        coordinatorDao.updateStatus(transId, status);
    }

    /**
     * 更新事务为失败状态
     *
     * @param mqthTransaction
     */
    @Override
    public void updateFailTransaction(MqthTransaction mqthTransaction) {
        coordinatorDao.updateFailTransaction(mqthTransaction);
    }

    @Override
    public List<MqthTransaction> listAllByDelay(Date date) {
        return coordinatorDao.listAllByDelay(date);
    }

    /**
     * 根据事务查询事务
     *
     * @param transId
     * @return
     */
    @Override
    public MqthTransaction findByTransId(String transId) {
        return coordinatorDao.findByTransId(transId);
    }


    /**
     * 获取项目模块名称
     *
     * @param repositorySuffix
     * @return
     */
    private String buildRepositorySuffix(String repositorySuffix) {
        if (StringUtils.isNoneBlank(repositorySuffix)) {
            return repositorySuffix;
        } else {
            return applicationService.acquireName();
        }
    }
}
