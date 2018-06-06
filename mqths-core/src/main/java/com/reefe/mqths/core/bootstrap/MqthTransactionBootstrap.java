package com.reefe.mqths.core.bootstrap;

import com.reefe.mqths.common.config.MqthConfig;
import com.reefe.mqths.core.helper.SpringBeanUtils;
import com.reefe.mqths.core.service.MqthInitService;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * 启动类 获取上下文
 * @Auther: REEFE
 * @Date: 2018/6/4/004
 */
public class MqthTransactionBootstrap extends MqthConfig implements ApplicationContextAware {

    private MqthInitService mqthInitService;

    @Autowired
    public MqthTransactionBootstrap(MqthInitService mqthInitService) {
        this.mqthInitService = mqthInitService;
    }


    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        //设置springboot 上下文对象
        SpringBeanUtils.getInstance().setCfgContext((ConfigurableApplicationContext) applicationContext);
        start(this);
    }

    private void start(MqthConfig mqthConfig) {
        mqthInitService.initialization(mqthConfig);
    }

}
