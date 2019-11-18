package com.atguigu.gmall.order.service;

import com.atguigu.gmall.oms.entity.OrderEntity;
import com.atguigu.gmall.oms.vo.OrderSubmitVO;
import com.atguigu.gmall.order.vo.OrderConfirmVO;

/**
 * @author eternity
 * @create 2019-11-15 20:11
 */
public interface OrderService {
    OrderConfirmVO confirm();

    OrderEntity submit(OrderSubmitVO orderSubmitVO);

    void paySuccess(String out_trade_no);
}
