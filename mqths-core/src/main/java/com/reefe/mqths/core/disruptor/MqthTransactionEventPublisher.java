package com.reefe.mqths.core.disruptor;

import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.YieldingWaitStrategy;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import com.reefe.mqths.common.bean.entity.MqthTransaction;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 异步事件发布者
 * @Auther: REEFE
 * @Date: 2018/6/5/005
 */
@Component
public class MqthTransactionEventPublisher implements DisposableBean {

    private Disruptor<MqthTransactionEvent> disruptor;

    @Autowired
    private MqthTransactionEventHandler mqthTransactionEventHandler;

    public void start(int bufferSize) {
       /* r -> {
            AtomicInteger index = new AtomicInteger(1);
            return new Thread(null, r, "disruptor-thread-" + index.getAndIncrement());
        }*/
        disruptor =
                new Disruptor<>(new MqthTransactionEventFactory(),
                        bufferSize, new ThreadFactory() {
                    @Override
                    public Thread newThread(Runnable r) {
                        AtomicInteger index = new AtomicInteger(1);
                        return new Thread(null, r, "disruptor-thread-" + index.getAndIncrement());
                    }
                }, ProducerType.MULTI, new YieldingWaitStrategy());

        disruptor.handleEventsWith(mqthTransactionEventHandler);
        disruptor.start();
    }


    /**
     * 发布事务
     * @param mqthTransaction
     * @param type
     */
    public void publishEvent(MqthTransaction mqthTransaction, int type) {
        final RingBuffer<MqthTransactionEvent> ringBuffer = disruptor.getRingBuffer();
        ringBuffer.publishEvent(new MqthTransactionEventTranslator(type), mqthTransaction);
    }


    @Override
    public void destroy() throws Exception {
        disruptor.shutdown();
    }
}
