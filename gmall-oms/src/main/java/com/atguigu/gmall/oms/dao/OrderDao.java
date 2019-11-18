package com.atguigu.gmall.oms.dao;

import com.atguigu.gmall.oms.entity.OrderEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 订单
 * 
 * @author huigege
 * @email 574059694@qq.com
 * @date 2019-10-28 23:29:48
 */
@Mapper
public interface OrderDao extends BaseMapper<OrderEntity> {

    int closeOrder(@Param("orderToken") String orderToken);

    int paySuccess(@Param("orderToken") String orderToken);
}
