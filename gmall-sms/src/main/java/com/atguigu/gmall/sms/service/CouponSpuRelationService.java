package com.atguigu.gmall.sms.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.atguigu.gmall.sms.entity.CouponSpuRelationEntity;
import com.atguigu.core.bean.PageVo;
import com.atguigu.core.bean.QueryCondition;


/**
 * 优惠券与产品关联
 *
 * @author huigege
 * @email 574059694@qq.com
 * @date 2019-10-28 23:20:36
 */
public interface CouponSpuRelationService extends IService<CouponSpuRelationEntity> {

    PageVo queryPage(QueryCondition params);
}

