package com.reefe.mqths.spingcloud.feign;

import com.reefe.mqths.common.annotation.Mqth;
import com.reefe.mqths.common.bean.context.MqthTransactionContext;
import com.reefe.mqths.common.bean.entity.MqthInvocation;
import com.reefe.mqths.common.bean.entity.MqthParticipant;
import com.reefe.mqths.core.concurrent.TransactionContextLocal;
import com.reefe.mqths.core.helper.SpringBeanUtils;
import com.reefe.mqths.core.manager.MqthTransactionManager;
import feign.InvocationHandlerFactory.MethodHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Objects;

/**
 * feign  负责中转jdk 动态代理对象 调用到对应的MethodHandler处理器上
 *
 * @Auther: REEFE
 * @Date: 2018/6/6/006
 */
public class MqthFeignHandler implements InvocationHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(MqthFeignHandler.class);
    private Object target;
    Map<Method, MethodHandler> handlers;

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (Object.class.equals(method.getDeclaringClass())) {
            return method.invoke(this, args);
        } else {
            final Mqth mqth = method.getAnnotation(Mqth.class);
            if (Objects.isNull(mqth)) {
                return this.handlers.get(method).invoke(args);
            }
            try {
                final MqthTransactionManager mqthTransactionManager =
                        SpringBeanUtils.getInstance().getBean(MqthTransactionManager.class);

                final MqthParticipant participant = buildParticipant(mqth, method, args);
                if (Objects.nonNull(participant)) {
                    mqthTransactionManager.registerParticipant(participant);
                }
                return this.handlers.get(method).invoke(args);
            } catch (Throwable throwable) {
                throwable.printStackTrace();
                return null;
            }
        }
    }

    /**
     * 构造参与者信息
     *
     * @param mqth
     * @param method
     * @param args
     * @return
     */

    private MqthParticipant buildParticipant(Mqth mqth, Method method, Object[] args) {
        //获取事务上下文
        MqthTransactionContext mqthTransactionContext = TransactionContextLocal.getInstance().get();
        MqthParticipant mqthParticipant;
        if (Objects.nonNull(mqthTransactionContext)) {
            Class target = mqth.target();
            MqthInvocation mqthInvocation = new MqthInvocation(target,
                    method.getName(),
                    method.getParameterTypes(), args);
            Integer pattern = mqth.pattern().getCode();
            mqthParticipant = new MqthParticipant(
                    mqthTransactionContext.getTransId(),
                    mqth.destination(),
                    pattern,
                    mqthInvocation);
            return mqthParticipant;
        }
        return null;
    }

    public void setTarget(Object target) {
        this.target = target;
    }

    public void setHandlers(Map<Method, MethodHandler> handlers) {
        this.handlers = handlers;
    }
}
