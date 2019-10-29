package com.atguigu.gmall.sms.dao;

import com.atguigu.gmall.sms.entity.CouponEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 优惠券信息
 * 
 * @author huigege
 * @email 574059694@qq.com
 * @date 2019-10-28 23:20:36
 */
@Mapper
public interface CouponDao extends BaseMapper<CouponEntity> {
	
}
