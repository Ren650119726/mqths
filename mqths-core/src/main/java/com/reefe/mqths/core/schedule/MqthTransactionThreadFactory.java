package com.reefe.mqths.core.schedule;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 线程工厂
 *
 * @Auther: REEFE
 * @Date: 2018/6/6/006
 */
public class MqthTransactionThreadFactory implements ThreadFactory {

    private static final Logger log = LoggerFactory.getLogger(MqthTransactionThreadFactory.class);

    private final AtomicLong threadNumber = new AtomicLong(1);

    private final String namePrefix;

    private static volatile boolean daemon;

    private static final ThreadGroup THREAD_GROUP = new ThreadGroup("MythTransaction");

    private MqthTransactionThreadFactory(String namePrefix, boolean daemon) {
        this.namePrefix = namePrefix;
        MqthTransactionThreadFactory.daemon = daemon;
    }


    public static ThreadGroup getThreadGroup() {
        return THREAD_GROUP;
    }

    public static ThreadFactory create(String namePrefix, boolean daemon) {
        return new MqthTransactionThreadFactory(namePrefix, daemon);
    }

    @Override
    public Thread newThread(Runnable runnable) {
        Thread thread = new Thread(THREAD_GROUP, runnable,
                THREAD_GROUP.getName() + "-" + namePrefix + "-" + threadNumber.getAndIncrement());
        thread.setDaemon(daemon);
        if (thread.getPriority() != Thread.NORM_PRIORITY) {
            thread.setPriority(Thread.NORM_PRIORITY);
        }
        return thread;
    }
}
