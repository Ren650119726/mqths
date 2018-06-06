package com.reefe.mqths.core.service;

import com.reefe.mqths.common.config.MqthConfig;

/**
 * @Auther: REEFE
 * @Date: 2018/6/4/004
 */
@FunctionalInterface //该注解只能标记在"有且仅有一个抽象方法（函数式接口）"的接口上。
public interface MqthInitService {
    /**
     * Mqth分布式事务初始化方法
     *
     * @param mqthConfig TCC配置
     */
    void initialization(MqthConfig mqthConfig);
}
