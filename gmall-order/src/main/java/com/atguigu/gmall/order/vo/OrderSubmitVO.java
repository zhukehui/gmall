package com.atguigu.gmall.order.vo;

import com.atguigu.gmall.ums.entity.MemberReceiveAddressEntity;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author eternity
 * @create 2019-11-15 22:47
 */
@Data
public class OrderSubmitVO {

    private MemberReceiveAddressEntity address;//收货地址

    private String orderToken;//提交上次订单确认页给你的令牌；(用于验价，防重，订单编号)

    private BigDecimal totalPrice; // 校验总价格时，拿计算价格和这个价格比较

    private Integer payType;//支付方式 0-在线支付  1-货到付款

    private String deliveryCompany; // 配送方式（配送公司）

    private List<OrderItemVO> orderItems; // 订单清单（订单详情）

    private Integer useIntegration;//下单时使用的积分

    // TODO：发票相关信息略

    // TODO：营销信息等

}
