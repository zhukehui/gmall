package com.atguigu.gmall.pms.vo;

import com.atguigu.gmall.pms.entity.SpuInfoEntity;
import lombok.Data;

import java.util.List;

/**
 * @author eternity
 * @create 2019-10-31 11:37
 */
@Data
public class SpuInfoVO extends SpuInfoEntity {
    private List<String> spuImages;

    private List<ProductAttrValueVO> baseAttrs;

    private List<SkuInfoVO> skus;
}
