package com.reefe.mqths.spingcloud.feign;

import feign.Feign;
import feign.InvocationHandlerFactory;
import feign.RequestInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

/**
 * @Auther: REEFE
 * @Date: 2018/6/6/006
 */
@Configuration
public class MqthFeignConfiguration {

    @Bean
    public Feign.Builder feignBuilder() {
       return Feign.builder().requestInterceptor(new MqthRequestTemplateInterceptor())
               .invocationHandlerFactory(invocationHandlerFactory());
    }

    @Bean
    public InvocationHandlerFactory invocationHandlerFactory() {
        return (target, dispatch) -> {
            MqthFeignHandler mqthFeignHandler = new MqthFeignHandler();
            mqthFeignHandler.setTarget(target);
            mqthFeignHandler.setHandlers(dispatch);
            return mqthFeignHandler;
        };
    }

}
