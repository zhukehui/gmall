package com.atguigu.gmall.wms.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.atguigu.gmall.wms.entity.FeightTemplateEntity;
import com.atguigu.core.bean.PageVo;
import com.atguigu.core.bean.QueryCondition;
import org.springframework.stereotype.Service;


/**
 * 运费模板
 *
 * @author huigege
 * @email 574059694@qq.com
 * @date 2019-10-28 23:06:04
 */
@Service
public interface FeightTemplateService extends IService<FeightTemplateEntity> {

    PageVo queryPage(QueryCondition params);
}

