package com.reefe.mqths.starter.config;

import com.reefe.mqths.common.config.MqthConfig;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 自定义启动加载配置文件
 * @author REEFE
 */
@Component
@ConfigurationProperties(prefix = "com.reefe.mqths")
public class MqthConfigProperties extends MqthConfig {

}
