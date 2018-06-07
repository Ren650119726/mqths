package com.reefe.mqths.starter.configuration;

import com.reefe.mqths.common.config.MqthConfig;
import com.reefe.mqths.core.bootstrap.MqthTransactionBootstrap;
import com.reefe.mqths.core.service.MqthInitService;
import com.reefe.mqths.starter.config.MqthConfigProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@Configuration
@EnableAspectJAutoProxy(proxyTargetClass = true)
@EnableConfigurationProperties
@ComponentScan(basePackages = {"com.reefe.mqths"})
public class MqthSpringcloudAutoConfiguration {

    private final static Logger logger = LoggerFactory.getLogger(MqthSpringcloudAutoConfiguration.class);

    @Autowired
    private MqthConfigProperties mqthConfigProperties;

    @Bean
    public MqthConfig mqthConfig() {
        return builder().build();
    }

    /**
     * 注册MqthTransactionBootstrap
     *
     * @param mqthInitService
     * @return
     */
    @Bean
    public MqthTransactionBootstrap mqthTransactionBootstrap(MqthInitService mqthInitService) {
        final MqthTransactionBootstrap bootstrap =
                new MqthTransactionBootstrap(mqthInitService);
        logger.debug("MqthConfig.builder Initialization MqthConfig");
        bootstrap.builder(builder());
        return bootstrap;
    }

    private MqthConfig.Builder builder() {
        logger.debug("MqthConfigProperties Initialization MqthConfig.builder ");
        return MqthTransactionBootstrap.create()
                .setSerializer(mqthConfigProperties.getSerializer())
                .setRepositorySuffix(mqthConfigProperties.getRepositorySuffix())
                .setRepositorySupport(mqthConfigProperties.getRepositorySupport())
                .setNeedRecover(mqthConfigProperties.getNeedRecover())
                .setBufferSize(mqthConfigProperties.getBufferSize())
                .setScheduledThreadMax(mqthConfigProperties.getScheduledThreadMax())
                .setScheduledDelay(mqthConfigProperties.getScheduledDelay())
                .setRetryMax(mqthConfigProperties.getRetryMax())
                .setRecoverDelayTime(mqthConfigProperties.getRecoverDelayTime())
                .setMqthDbConfig(mqthConfigProperties.getMqthDbConfig());
    }

    
}  