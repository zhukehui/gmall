package com.atguigu.gmall.sms.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.atguigu.gmall.sms.entity.SpuBoundsEntity;
import com.atguigu.core.bean.PageVo;
import com.atguigu.core.bean.QueryCondition;


/**
 * 商品spu积分设置
 *
 * @author huigege
 * @email 574059694@qq.com
 * @date 2019-10-28 23:20:36
 */
public interface SpuBoundsService extends IService<SpuBoundsEntity> {

    PageVo queryPage(QueryCondition params);
}

