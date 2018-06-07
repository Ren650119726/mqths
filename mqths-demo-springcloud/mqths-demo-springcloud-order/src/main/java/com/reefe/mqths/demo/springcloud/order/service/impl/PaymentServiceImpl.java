/*
 *
 * Copyright 2017-2018 549477611@qq.com(xiaoyu)
 *
 * This copyrighted material is made available to anyone wishing to use, modify,
 * copy, or redistribute it subject to the terms and conditions of the GNU
 * Lesser General Public License, as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
 * for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this distribution; if not, see <http://www.gnu.org/licenses/>.
 *
 */

package com.reefe.mqths.demo.springcloud.order.service.impl;


import com.reefe.mqths.common.annotation.Mqth;
import com.reefe.mqths.common.exception.MqthRuntimeException;
import com.reefe.mqths.demo.springcloud.account.api.dto.AccountDTO;
import com.reefe.mqths.demo.springcloud.account.api.entity.AccountDO;
import com.reefe.mqths.demo.springcloud.inventory.api.dto.InventoryDTO;
import com.reefe.mqths.demo.springcloud.inventory.api.entity.InventoryDO;
import com.reefe.mqths.demo.springcloud.order.client.AccountClient;
import com.reefe.mqths.demo.springcloud.order.client.InventoryClient;
import com.reefe.mqths.demo.springcloud.order.entity.Order;
import com.reefe.mqths.demo.springcloud.order.enums.OrderStatusEnum;
import com.reefe.mqths.demo.springcloud.order.mapper.OrderMapper;
import com.reefe.mqths.demo.springcloud.order.service.PaymentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author xiaoyu
 */
@Service
public class PaymentServiceImpl implements PaymentService {


    /**
     * logger
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(PaymentServiceImpl.class);

    private final OrderMapper orderMapper;


    private final AccountClient accountClient;

    private final InventoryClient inventoryClient;

    @Autowired(required = false)
    public PaymentServiceImpl(OrderMapper orderMapper, AccountClient accountClient, InventoryClient inventoryClient) {
        this.orderMapper = orderMapper;
        this.accountClient = accountClient;
        this.inventoryClient = inventoryClient;
    }


    @Override
    @Mqth(destination = "")
    public void makePayment(Order order) {


        //检查数据 这里只是demo 只是demo 只是demo

        final AccountDO accountDO =
                accountClient.findByUserId(order.getUserId());

        if (accountDO.getBalance().compareTo(order.getTotalAmount()) <= 0) {
            throw new MqthRuntimeException("余额不足！");
        }

        final InventoryDO inventoryDO =
                inventoryClient.findByProductId(order.getProductId());

        if (inventoryDO.getTotalInventory() < order.getCount()) {
            throw new MqthRuntimeException("库存不足！");
        }

        order.setStatus(OrderStatusEnum.PAY_SUCCESS.getCode());
        orderMapper.update(order);
        //扣除用户余额
        AccountDTO accountDTO = new AccountDTO();
        accountDTO.setAmount(order.getTotalAmount());
        accountDTO.setUserId(order.getUserId());

        accountClient.payment(accountDTO);

        //进入扣减库存操作
        InventoryDTO inventoryDTO = new InventoryDTO();
        inventoryDTO.setCount(order.getCount());
        inventoryDTO.setProductId(order.getProductId());
        inventoryClient.decrease(inventoryDTO);
    }

}
