package com.reefe.mqths.common.config;

import com.reefe.mqths.common.enums.RepositorySupportEnum;
import com.reefe.mqths.common.enums.SerializeEnum;

/**
 * 全局配置信息
 * @author REEFE
 */

public class MqthConfig {
    /**
     * 资源后缀  此参数请填写  关于是事务存储路径
     * 1 如果是表存储 这个就是表名后缀，其他方式存储一样
     * 2 如果此参数不填写，那么会默认获取应用的applicationName
     */
    private String repositorySuffix;

    /**
     * 提供不同的序列化对象 {@linkplain SerializeEnum}
     */
    private String serializer = "kryo";


    /**
     * 补偿存储类型 {@linkplain RepositorySupportEnum}
     */
    private String repositorySupport = "db";


    /**
     * 任务调度
     *
     * 是否需要自动恢复
     * 1 注意 当为事务发起方的时候（调用方/消费方），这里需要填true，
     * 默认为false，为了节省资源，不开启线程池调度
     */
    private Boolean needRecover = false;

    /**
     * 任务调度线程大小
     */
    private int scheduledThreadMax = Runtime.getRuntime().availableProcessors() << 1;

    /**
     * 调度时间周期 单位秒
     */
    private int scheduledDelay = 60;

    /**
     * 最大重试次数
     */
    private int retryMax = 3;

    /**
     * 事务恢复间隔时间 单位秒（注意 此时间表示本地事务创建的时间多少秒以后才会执行）
     */
    private int recoverDelayTime = 60;

    /**
     * disruptor  bufferSize
     */
    private int bufferSize = 1024;

    /**
     * db配置
     */
    private MqthDbConfig mqthDbConfig;

    public MqthConfig() {
    }

    public MqthConfig(Builder builder) {
        builder(builder);
    }

    public static Builder create() {
        return new Builder();
    }

    public void builder(Builder builder) {
        this.serializer = builder.serializer;
        this.repositorySuffix = builder.repositorySuffix;
        this.repositorySupport = builder.repositorySupport;
        this.needRecover = builder.needRecover;
        this.scheduledThreadMax = builder.scheduledThreadMax;
        this.scheduledDelay = builder.scheduledDelay;
        this.retryMax = builder.retryMax;
        this.recoverDelayTime = builder.recoverDelayTime;
        this.bufferSize = builder.bufferSize;
        this.mqthDbConfig = builder.mqthDbConfig;
    }


    /**
     * Builder模式
     */
    public static class Builder {
        private String repositorySuffix;

        private String serializer = "kryo";

        private String repositorySupport = "db";

        private Boolean needRecover = false;

        private int scheduledThreadMax = Runtime.getRuntime().availableProcessors() << 1;

        private int scheduledDelay = 60;

        private int retryMax = 3;

        private int recoverDelayTime = 60;

        private int bufferSize = 1024;

        private MqthDbConfig mqthDbConfig;

        public Builder setRepositorySuffix(String repositorySuffix) {
            this.repositorySuffix = repositorySuffix;
            return this;
        }

        public Builder setSerializer(String serializer) {
            this.serializer = serializer;
            return this;
        }

        public Builder setRepositorySupport(String repositorySupport) {
            this.repositorySupport = repositorySupport;
            return this;
        }

        public Builder setNeedRecover(Boolean needRecover) {
            this.needRecover = needRecover;
            return this;
        }

        public Builder setScheduledThreadMax(int scheduledThreadMax) {
            this.scheduledThreadMax = scheduledThreadMax;
            return this;
        }

        public Builder setScheduledDelay(int scheduledDelay) {
            this.scheduledDelay = scheduledDelay;
            return this;
        }

        public Builder setRetryMax(int retryMax) {
            this.retryMax = retryMax;
            return this;
        }

        public Builder setRecoverDelayTime(int recoverDelayTime) {
            this.recoverDelayTime = recoverDelayTime;
            return this;
        }

        public Builder setBufferSize(int bufferSize) {
            this.bufferSize = bufferSize;
            return this;
        }

        public Builder setMqthDbConfig(MqthDbConfig mqthDbConfig){
            this.mqthDbConfig = mqthDbConfig;
            return this;
        }

        public String getRepositorySuffix() {
            return repositorySuffix;
        }

        public String getSerializer() {
            return serializer;
        }

        public String getRepositorySupport() {
            return repositorySupport;
        }

        public Boolean getNeedRecover() {
            return needRecover;
        }

        public int getScheduledThreadMax() {
            return scheduledThreadMax;
        }

        public int getScheduledDelay() {
            return scheduledDelay;
        }

        public int getRetryMax() {
            return retryMax;
        }

        public int getRecoverDelayTime() {
            return recoverDelayTime;
        }

        public int getBufferSize() {
            return bufferSize;
        }

        public MqthDbConfig getMqthDbConfig() {
            return mqthDbConfig;
        }

        public MqthConfig build() {
            return new MqthConfig(this);
        }

    }

    public String getRepositorySuffix() {
        return repositorySuffix;
    }

    public String getSerializer() {
        return serializer;
    }

    public String getRepositorySupport() {
        return repositorySupport;
    }

    public Boolean getNeedRecover() {
        return needRecover;
    }

    public int getScheduledThreadMax() {
        return scheduledThreadMax;
    }

    public int getScheduledDelay() {
        return scheduledDelay;
    }

    public int getRetryMax() {
        return retryMax;
    }

    public int getRecoverDelayTime() {
        return recoverDelayTime;
    }

    public int getBufferSize() {
        return bufferSize;
    }

    public MqthDbConfig getMqthDbConfig() {
        return mqthDbConfig;
    }

    public void setRepositorySuffix(String repositorySuffix) {
        this.repositorySuffix = repositorySuffix;
    }

    public void setSerializer(String serializer) {
        this.serializer = serializer;
    }

    public void setRepositorySupport(String repositorySupport) {
        this.repositorySupport = repositorySupport;
    }

    public void setNeedRecover(Boolean needRecover) {
        this.needRecover = needRecover;
    }

    public void setScheduledThreadMax(int scheduledThreadMax) {
        this.scheduledThreadMax = scheduledThreadMax;
    }

    public void setScheduledDelay(int scheduledDelay) {
        this.scheduledDelay = scheduledDelay;
    }

    public void setRetryMax(int retryMax) {
        this.retryMax = retryMax;
    }

    public void setRecoverDelayTime(int recoverDelayTime) {
        this.recoverDelayTime = recoverDelayTime;
    }

    public void setBufferSize(int bufferSize) {
        this.bufferSize = bufferSize;
    }

    public void setMqthDbConfig(MqthDbConfig mqthDbConfig) {
        this.mqthDbConfig = mqthDbConfig;
    }
}
