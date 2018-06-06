package com.reefe.mqths.spingcloud.interceptor;

import com.reefe.mqths.common.bean.context.MqthTransactionContext;
import com.reefe.mqths.common.constant.CommonConstant;
import com.reefe.mqths.common.enums.MqthRoleEnum;
import com.reefe.mqths.common.utils.GsonUtils;
import com.reefe.mqths.core.concurrent.TransactionContextLocal;
import com.reefe.mqths.core.interceptor.MqthTransactionInterceptor;
import com.reefe.mqths.core.service.MqthTransactionAspectService;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Objects;

/**
 * 事务拦截器 Springcloud 具体实现
 *
 * @Auther: REEFE
 * @Date: 2018/6/5/005
 */
@Component
public class SpringCloudMqthTransactionInterceptor implements MqthTransactionInterceptor {

    @Autowired
    private MqthTransactionAspectService mqthTransactionAspectService;


    /**
     * 分布式事务拦截方法
     *
     * @param pjp tcc切入点
     * @return Object
     * @throws Throwable 异常
     */
    @Override
    public Object interceptor(ProceedingJoinPoint pjp) throws Throwable {
        //获取事务上下文
        MqthTransactionContext mqthTransactionContext = TransactionContextLocal.getInstance().get();
        if (Objects.nonNull(mqthTransactionContext)
                && mqthTransactionContext.getRole() == MqthRoleEnum.LOCAL.getCode()) {
            mqthTransactionContext = TransactionContextLocal.getInstance().get();
        } else {
            RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
            HttpServletRequest request = requestAttributes == null ? null :
                    ((ServletRequestAttributes) requestAttributes).getRequest();
            String context = request == null ? null : request.getHeader(CommonConstant.MQTH_TRANSACTION_CONTEXT);
            if (StringUtils.isNoneBlank(context)) {
                mqthTransactionContext = GsonUtils.fromJson(context, MqthTransactionContext.class);
            }
        }
        return mqthTransactionAspectService.invoke(mqthTransactionContext, pjp);
    }
}
