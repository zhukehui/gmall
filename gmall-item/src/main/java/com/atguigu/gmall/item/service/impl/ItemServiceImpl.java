package com.atguigu.gmall.item.service.impl;

import com.atguigu.core.bean.Resp;
import com.atguigu.gmall.item.feign.GmallPmsClient;
import com.atguigu.gmall.item.feign.GmallSmsClient;
import com.atguigu.gmall.item.feign.GmallWmsClient;
import com.atguigu.gmall.item.service.ItemService;
import com.atguigu.gmall.item.vo.ItemVO;
import com.atguigu.gmall.pms.api.GmallPmsApi;
import com.atguigu.gmall.pms.entity.*;
import com.atguigu.gmall.pms.vo.GroupVO;
import com.atguigu.gmall.sms.vo.ItemSaleVO;
import com.atguigu.gmall.wms.entity.WareSkuEntity;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author eternity
 * @create 2019-11-10 10:44
 */
@Service
public class ItemServiceImpl implements ItemService {

    @Autowired
    private GmallPmsClient gmallPmsClient;
    @Autowired
    private GmallSmsClient gmallSmsClient;
    @Autowired
    private GmallWmsClient gmallWmsClient;

    @Override
    public ItemVO item(Long skuId) {

        ItemVO itemVO = new ItemVO();
        //查询sku信息
        Resp<SkuInfoEntity> skuInfoEntityResp = this.gmallPmsClient.querySkuById(skuId);
        SkuInfoEntity skuInfoEntity = skuInfoEntityResp.getData();
        BeanUtils.copyProperties(skuInfoEntity,itemVO);
        Long spuId = skuInfoEntity.getSpuId();
        //品牌
        Resp<BrandEntity> brandEntityResp = this.gmallPmsClient.queryBrandById(skuInfoEntity.getBrandId());

        itemVO.setBrand(brandEntityResp.getData());

        //分类
        Resp<CategoryEntity> categoryEntityResp = this.gmallPmsClient.queryCategoryById(skuInfoEntity.getCatalogId());

        itemVO.setCategory(categoryEntityResp.getData());

        //spu信息
        Resp<SpuInfoEntity> spuInfoEntityResp = this.gmallPmsClient.querySpuById(spuId);

        itemVO.setSpuInfo(spuInfoEntityResp.getData());

        //图片信息
        Resp<List<String>> picsResp = this.gmallPmsClient.queryPicsBySkuId(skuId);
        itemVO.setPics(picsResp.getData());

        //营销信息
        Resp<List<ItemSaleVO>> itemSaleResp = this.gmallSmsClient.queryItemSaleVOs(skuId);
        itemVO.setSales(itemSaleResp.getData());

        //是否有货
        Resp<List<WareSkuEntity>> wareSkuResp = this.gmallWmsClient.queryWareSkuBySkuId(skuId);
        List<WareSkuEntity> wareSkuEntities = wareSkuResp.getData();

        itemVO.setStore(wareSkuEntities.stream().anyMatch(t -> t.getStock() > 0));

        //spu所有的销售属性
        Resp<List<SkuSaleAttrValueEntity>> saleAttrValueResp = this.gmallPmsClient.querySaleAttrValues(spuId);
        itemVO.setSkuSales(saleAttrValueResp.getData());

        //spu的描述信息
        Resp<SpuInfoDescEntity> spuInfoDescEntityResp = this.gmallPmsClient.querySpuDescById(spuId);

        itemVO.setDesc(spuInfoDescEntityResp.getData());

        //规格属性分组及组下的规格参数及值
        Resp<List<GroupVO>> listResp = this.gmallPmsClient.queryGroupVOByCid(skuInfoEntity.getCatalogId(), spuId);

        itemVO.setGroups(listResp.getData());

        return itemVO;
    }
}
