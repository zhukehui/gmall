package com.atguigu.gmall.pms.service.impl;

import com.atguigu.core.bean.PageVo;
import com.atguigu.core.bean.Query;
import com.atguigu.core.bean.QueryCondition;
import com.atguigu.gmall.pms.dao.*;
import com.atguigu.gmall.pms.entity.*;
import com.atguigu.gmall.pms.feign.GmallSmsClient;
import com.atguigu.gmall.pms.service.SpuInfoDescService;
import com.atguigu.gmall.pms.service.SpuInfoService;
import com.atguigu.gmall.pms.vo.ProductAttrValueVO;
import com.atguigu.gmall.pms.vo.SkuInfoVO;
import com.atguigu.gmall.pms.vo.SpuInfoVO;
import com.atguigu.gmall.sms.vo.SaleVO;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.seata.spring.annotation.GlobalTransactional;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.Date;
import java.util.List;
import java.util.UUID;


@Service("spuInfoService")
public class SpuInfoServiceImpl extends ServiceImpl<SpuInfoDao, SpuInfoEntity> implements SpuInfoService {

    @Autowired
    private SpuInfoDescDao descDao;

    @Autowired
    private ProductAttrValueDao productAttrValueDao;
    @Autowired
    private SkuInfoDao skuInfoDao;
    @Autowired
    private SkuImagesDao skuImagesDao;

    @Autowired
    private SkuSaleAttrValueDao skuSaleAttrValueDao;
    @Autowired
    private GmallSmsClient smsClient;
    @Autowired
    private SpuInfoDescService spuInfoDescService;


    @Override
    public PageVo queryPage(QueryCondition params) {
        IPage<SpuInfoEntity> page = this.page(
                new Query<SpuInfoEntity>().getPage(params),
                new QueryWrapper<SpuInfoEntity>()
        );

        return new PageVo(page);
    }

    @Override
    public PageVo querySpuInfo(QueryCondition condition, Long catId) {

        //封装分页条件
        IPage<SpuInfoEntity> page = new Query<SpuInfoEntity>().getPage(condition);

        //封装查询条件
        QueryWrapper<SpuInfoEntity> wrapper = new QueryWrapper<>();
        // 如果分类id不为0，要根据分类id查，否则查全部
        if (catId != 0){
            wrapper.eq("catalog_id",catId);
        }

        //如果用户输入了检索条件，根据检索条件查询
        String key = condition.getKey();
        if (StringUtils.isNoneBlank(key)){
            wrapper.and(t -> t.like("id",key).or().like("spu_name",key));
        }
        return new PageVo(this.page(page,wrapper));
    }


    /**
     * 九张表
     * 1.spu相关的：3张
     * 2.sku相关的：3张
     * 3.营销相关：3张
     * @param spuInfoVO
     */

    /**
     * 在类中获取代理对象分三个步骤：
     *
     * 1. 导入aop的场景依赖：spring-boot-starter-aop
     * 2. 开启AspectJ的自动代理，同时要暴露代理对象：@EnableAspectJAutoProxy(exposeProxy=true)
     * 3. 获取代理对象：SpuInfoService proxy = (SpuInfoService) AopContext.currentProxy();
     */
//    @Transactional
    @GlobalTransactional
    @Override
    public void bigSave(SpuInfoVO spuInfoVO) {

        //1.新增spu相关的3张表
        //1.1 新增spuInfo
        Long spuId = saveSpuInfo(spuInfoVO);

        //1.2 新增spuInfoDesc 保存spu的描述信息 spu_info_desc
//        this.saveSpuDesc(spuInfoVO, spuId);
        this.spuInfoDescService.saveSpuDesc(spuInfoVO,spuId);

       /* try {
            TimeUnit.SECONDS.sleep(4);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }*/
        //1.3 新增基本属性 productAttrValue
        this.saveBaseAttrs(spuInfoVO, spuId);

        //2.新增sku相关的3张表：spuID
        this.saveSku(spuInfoVO, spuId);

//        int i = 1 / 0 ;

    }

    /**
     * 保存sku相关信息及营销信息
     * @param spuInfoVO
     * @param spuId
     */
    private void saveSku(SpuInfoVO spuInfoVO, Long spuId) {
        List<SkuInfoVO> skus = spuInfoVO.getSkus();
        if (CollectionUtils.isEmpty(skus)){
            return;
        }
        //2.1 新增skuInfo
        skus.forEach(skuInfoVO -> {
            SkuInfoEntity skuInfoEntity = new SkuInfoEntity();
            BeanUtils.copyProperties(skuInfoVO,skuInfoEntity);
            // 品牌和分类的id需要从spuInfo中获取
            skuInfoEntity.setBrandId(spuInfoVO.getBrandId());
            skuInfoEntity.setCatalogId(spuInfoVO.getCatalogId());

            // 获取随机的uuid作为sku的编码
            skuInfoEntity.setSkuCode(UUID.randomUUID().toString());
            skuInfoEntity.setSpuId(spuId);
            // 获取图片列表
            List<String> images = skuInfoVO.getImages();
            //设置默认图片
            if (!CollectionUtils.isEmpty(images)){

                skuInfoEntity.setSkuDefaultImg
                        (StringUtils.isNoneBlank(skuInfoEntity.getSkuDefaultImg())? skuInfoEntity.getSkuDefaultImg():images.get(0));
            }
            this.skuInfoDao.insert(skuInfoEntity);

            Long skuId = skuInfoEntity.getSkuId();
            //2.2 新增sku的图片
            if (!CollectionUtils.isEmpty(images)){
                images.forEach(image -> {

                    SkuImagesEntity skuImagesEntity = new SkuImagesEntity();
                    skuImagesEntity.setSkuId(skuId);
                    skuImagesEntity.setDefaultImg(StringUtils.equals(image,skuInfoEntity.getSkuDefaultImg())? 1 : 0);
                    skuImagesEntity.setImgSort(0);
                    skuImagesEntity.setImgUrl(image);

                    this.skuImagesDao.insert(skuImagesEntity);
                });
            }
            //2.3 新增的销售属性

            List<SkuSaleAttrValueEntity> saleAttrs = skuInfoVO.getSaleAttrs();
            if (!CollectionUtils.isEmpty(saleAttrs)){
                saleAttrs.forEach(saleAttr -> {
                    saleAttr.setSkuId(skuId);
                    saleAttr.setAttrSort(0);
                    this.skuSaleAttrValueDao.insert(saleAttr);
                });
            }

            //3.新增营销相关的3张表：skuId
            SaleVO saleVO = new SaleVO();
            BeanUtils.copyProperties(skuInfoVO,saleVO);
            saleVO.setSkuId(skuId);
            this.smsClient.saveSale(saleVO);

        });
    }

    /**
     * 保存spu基本属性信息
     * @param spuInfoVO
     * @param spuId
     */
    private void saveBaseAttrs(SpuInfoVO spuInfoVO, Long spuId) {
        List<ProductAttrValueVO> baseAttrs = spuInfoVO.getBaseAttrs();

        baseAttrs.forEach(baseAttr -> {
            baseAttr.setSpuId(spuId);
            baseAttr.setAttrSort(0);
            baseAttr.setQuickShow(1);
            this.productAttrValueDao.insert(baseAttr);
        });
    }


    /**
     * 保存spu基本信息
     * @param spuInfoVO
     * @return
     */
    @Transactional
    public Long saveSpuInfo(SpuInfoVO spuInfoVO) {
        spuInfoVO.setPublishStatus(1); // 默认是已上架
        spuInfoVO.setCreateTime(new Date());
        spuInfoVO.setUodateTime(spuInfoVO.getCreateTime());// 新增时，更新时间和创建时间一致
        this.save(spuInfoVO);

        return spuInfoVO.getId();
    }

}