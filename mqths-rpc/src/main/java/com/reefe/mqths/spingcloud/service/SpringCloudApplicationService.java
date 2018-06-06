package com.reefe.mqths.spingcloud.service;

import com.reefe.mqths.core.service.ApplicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * SpringCloud Application
 * @Auther: REEFE
 * @Date: 2018/6/5/005
 */
@Service("applicationService")
public class SpringCloudApplicationService implements ApplicationService {
    @Value("${spring.application.name}")
    private String appName;

    /**
     * 获取applicationName 项目名
     *
     * @return applicationName
     */
    @Override
    public String acquireName() {
        return appName;
    }
}
