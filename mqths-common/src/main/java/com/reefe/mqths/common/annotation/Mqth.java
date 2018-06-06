package com.reefe.mqths.common.annotation;

import com.reefe.mqths.common.enums.MessageTypeEnum;
import com.reefe.mqths.common.enums.PropagationEnum;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 分布式事务注解
 * @Auther: REEFE
 * @Date: 2018/6/5/005
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Mqth {
    /**
     * 消息队列的唯一标识 (在rocketmq或者aliyunmq中是topic tags 以","分隔)
     */
    String destination();

    /**
     * rocketmq特有的tag区分方式,tags的值需要完全满足rocketmq规则
     *
     * @return
     */
    String tags() default "";

    /**
     * 目标接口类
     * 如果是springcloud用户，需要指定目标的接口服务
     * （因为springcloud是http的请求，通过反射序列化方式没办法调用，所有加了这个属性）
     * 如果是dubbo用户 则不需要指定
     * 如果是motan用户 则不需要指定
     *
     * @return Class
     */
    Class target() default Object.class;


    /**
     * 目标接口方法名称
     * 如果是springcloud用户，需要指定目标的方法名称
     * （因为springcloud是http的请求，通过反射序列化方式没办法调用，所有加了这个属性）
     * 如果是dubbo用户 则不需要指定
     * 如果是motan用户 则不需要指定
     *
     * @return String
     */
    String targetMethod() default "";


    /**
     * 是否有事务 这里具体指的是发起方是否有进行数据库的操作（是否有事务操作）
     *
     * @return PropagationEnum
     */
//    PropagationEnum propagation() default PropagationEnum.PROPAGATION_REQUIRED;


    /**
     * mq 消息模式
     *
     * @return MessageTypeEnum
     */
    MessageTypeEnum pattern() default MessageTypeEnum.P2P;


}
