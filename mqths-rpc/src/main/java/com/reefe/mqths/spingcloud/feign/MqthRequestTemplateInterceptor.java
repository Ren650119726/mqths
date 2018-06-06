package com.reefe.mqths.spingcloud.feign;

import com.reefe.mqths.common.bean.context.MqthTransactionContext;
import com.reefe.mqths.common.constant.CommonConstant;
import com.reefe.mqths.common.utils.GsonUtils;
import com.reefe.mqths.core.concurrent.TransactionContextLocal;
import feign.RequestInterceptor;
import feign.RequestTemplate;

/**
 * feign 自定义拦截器 统一添加请求头
 *
 * @Auther: REEFE
 * @Date: 2018/6/6/006
 */
public class MqthRequestTemplateInterceptor implements RequestInterceptor {

    @Override
    public void apply(RequestTemplate requestTemplate) {
        MqthTransactionContext mqthTransactionContext = TransactionContextLocal.getInstance().get();
        requestTemplate.header(CommonConstant.MQTH_TRANSACTION_CONTEXT, GsonUtils.toJson(mqthTransactionContext));
    }
}
