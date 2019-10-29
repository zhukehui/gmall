package com.atguigu.gmall.sms.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.atguigu.gmall.sms.entity.HomeSubjectEntity;
import com.atguigu.core.bean.PageVo;
import com.atguigu.core.bean.QueryCondition;


/**
 * 首页专题表【jd首页下面很多专题，每个专题链接新的页面，展示专题商品信息】
 *
 * @author huigege
 * @email 574059694@qq.com
 * @date 2019-10-28 23:20:36
 */
public interface HomeSubjectService extends IService<HomeSubjectEntity> {

    PageVo queryPage(QueryCondition params);
}

