package com.atguigu.gmall.pms.vo;

import com.atguigu.gmall.pms.entity.ProductAttrValueEntity;
import lombok.Data;

import java.util.List;

/**
 * @author eternity
 * @create 2019-11-09 22:22
 */
@Data
public class GroupVO {

    private String gorupName; //分组的名字

    private List<ProductAttrValueEntity> baseAttrValues;

}
