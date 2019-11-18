package com.atguigu.gmall.oms.dao;

import com.atguigu.gmall.oms.entity.OrderItemEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单项信息
 * 
 * @author huigege
 * @email 574059694@qq.com
 * @date 2019-10-28 23:29:48
 */
@Mapper
public interface OrderItemDao extends BaseMapper<OrderItemEntity> {

}
