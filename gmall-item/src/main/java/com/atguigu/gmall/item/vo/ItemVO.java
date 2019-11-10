package com.atguigu.gmall.item.vo;

import com.atguigu.gmall.pms.entity.*;
import com.atguigu.gmall.pms.vo.GroupVO;
import com.atguigu.gmall.sms.vo.ItemSaleVO;
import lombok.Data;

import java.util.List;

/**
 * @author eternity
 * @create 2019-11-09 22:10
 */
@Data
public class ItemVO extends SkuInfoEntity {

    //当前sku的基本信息
    private SpuInfoEntity spuInfo;

    private BrandEntity brand;

    private CategoryEntity category;

    private List<String> pics; //sku的所有图片列表

    private List<ItemSaleVO> sales; //营销信息

    private Boolean store; //是否有货

    private List<SkuSaleAttrValueEntity> skuSales; //spu下所有sku的所有销售属性组合

    private SpuInfoDescEntity desc; //详情介绍

    private List<GroupVO> groups; //组及组下的规格属性及值
}
