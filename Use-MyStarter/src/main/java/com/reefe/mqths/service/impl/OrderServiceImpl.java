package com.reefe.mqths.service.impl;

import com.reefe.mqths.common.annotation.Mqth;
import com.reefe.mqths.service.OrderSerivce;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * @Auther: REEFE
 * @Date: 2018/6/6/006
 */
@Service
public class OrderServiceImpl implements OrderSerivce {

    /**
     * 创建订单并且进行扣除账户余额支付，并进行库存扣减操作
     *
     * @param count  购买数量
     * @param amount 支付金额
     * @return string
     */
    @Override
    @Mqth(destination = "test",tags = "test1")
    public String orderPay(Integer count, BigDecimal amount) {
        System.out.println("orderPay");
        return "";
    }
}
