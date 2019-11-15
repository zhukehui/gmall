package com.atguigu.gmall.wms.service.impl;

import com.atguigu.gmall.wms.vo.SkuLockVO;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.core.bean.PageVo;
import com.atguigu.core.bean.Query;
import com.atguigu.core.bean.QueryCondition;

import com.atguigu.gmall.wms.dao.WareSkuDao;
import com.atguigu.gmall.wms.entity.WareSkuEntity;
import com.atguigu.gmall.wms.service.WareSkuService;
import org.springframework.util.CollectionUtils;


@Service("wareSkuService")
public class WareSkuServiceImpl extends ServiceImpl<WareSkuDao, WareSkuEntity> implements WareSkuService {

    @Autowired
    private RedissonClient redissonClient;

    @Autowired
    private WareSkuDao wareSkuDao;

    @Override
    public PageVo queryPage(QueryCondition params) {
        IPage<WareSkuEntity> page = this.page(
                new Query<WareSkuEntity>().getPage(params),
                new QueryWrapper<WareSkuEntity>()
        );

        return new PageVo(page);
    }

    @Override
    public String checkAndLock(List<SkuLockVO> skuLockVOS) {


        //遍历
        skuLockVOS.forEach(skuLockVO -> {
            lockSku(skuLockVO);
        });

        //查看有没有失败的记录
        //有失败的记录。则回滚成功的记录
        List<SkuLockVO> success = skuLockVOS.stream().filter(skuLockVO -> skuLockVO.getLock()).collect(Collectors.toList());
        List<SkuLockVO> error = skuLockVOS.stream().filter(skuLockVO -> !skuLockVO.getLock()).collect(Collectors.toList());

        if (!CollectionUtils.isEmpty(error)){
            success.forEach(skuLockVO -> {
                this.wareSkuDao.unLock(skuLockVO.getSkuWareId(),skuLockVO.getCount());
            });
            return "锁定失败" + error.stream().map(skuLockVO -> skuLockVO.getSkuId()).collect(Collectors.toList()).toString();
        }


        return null;
    }

    private void lockSku(SkuLockVO skuLockVO){

        RLock lock = this.redissonClient.getLock("sku:lock:" + skuLockVO.getSkuId());
        lock.lock();
        //验库存（查询符合购买数量库存的仓库）
        List<WareSkuEntity> wareSkuEntities = this.wareSkuDao.checkStore(skuLockVO.getSkuId(), skuLockVO.getCount());

        skuLockVO.setLock(false);//若没有任何一个仓库的库存数满足要求，则锁定失败


        if (!CollectionUtils.isEmpty(wareSkuEntities)){

            //锁库存（如果有符合条件的仓库取第一个）

            if (this.wareSkuDao.lock(wareSkuEntities.get(0).getId(), skuLockVO.getCount()) == 1){
                skuLockVO.setLock(true);
                skuLockVO.setSkuWareId(wareSkuEntities.get(0).getId());
            }
        }

        lock.unlock();
    }

}