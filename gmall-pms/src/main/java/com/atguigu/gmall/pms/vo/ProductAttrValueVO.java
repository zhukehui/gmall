package com.atguigu.gmall.pms.vo;

import com.atguigu.gmall.pms.entity.ProductAttrValueEntity;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * @author eternity
 * @create 2019-10-31 11:41
 */

public class ProductAttrValueVO extends ProductAttrValueEntity {

    public void setValueSelected(List<String> valueSelected){

        this.setAttrValue(StringUtils.join(valueSelected,","));
    }
}
