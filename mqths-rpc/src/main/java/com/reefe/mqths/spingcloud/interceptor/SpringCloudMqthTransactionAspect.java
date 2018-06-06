package com.reefe.mqths.spingcloud.interceptor;

import com.reefe.mqths.core.interceptor.AbstractMqthTransactionAspect;
import com.reefe.mqths.core.interceptor.MqthTransactionInterceptor;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;

/**
 * @Auther: REEFE
 * @Date: 2018/6/5/005
 */
@Aspect
@Component
public class SpringCloudMqthTransactionAspect extends AbstractMqthTransactionAspect implements Ordered{

    @Autowired
    public SpringCloudMqthTransactionAspect(SpringCloudMqthTransactionInterceptor springCloudMqthTransactionInterceptor) {
        this.setMqthTransactionInterceptor(springCloudMqthTransactionInterceptor);
    }

    /**
     * spring Order 接口，该值的返回直接会影响springBean的加载顺序
     *
     * @return int 类型
     */
    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
}
