package com.reefe.mqths.common.bean.entity;

import java.io.Serializable;

/**
 * 执行器 反射调用方法 保证数据最终一致性
 * @Auther: REEFE
 * @Date: 2018/6/5/005
 */
public class MqthInvocation implements Serializable{

    private static final long serialVersionUID = 4784771610393301993L;
    /**
     * class
     */
    private Class targetClass;

    /**
     * methodName 方法名
     */
    private String methodName;

    /**
     * 参数类型
     */
    private Class[] parameterTypes;

    /**
     * 参数
     */
    private Object[] args;

    public MqthInvocation() {
    }

    public MqthInvocation(Class targetClass, String methodName, Class[] parameterTypes, Object[] args) {
        this.targetClass = targetClass;
        this.methodName = methodName;
        this.parameterTypes = parameterTypes;
        this.args = args;
    }

    public Class getTargetClass() {
        return targetClass;
    }

    public String getMethodName() {
        return methodName;
    }

    public Class[] getParameterTypes() {
        return parameterTypes;
    }

    public Object[] getArgs() {
        return args;
    }
}
