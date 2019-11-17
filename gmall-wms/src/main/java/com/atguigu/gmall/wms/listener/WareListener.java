package com.atguigu.gmall.wms.listener;

import com.alibaba.fastjson.JSON;
import com.atguigu.gmall.wms.dao.WareSkuDao;
import com.atguigu.gmall.wms.vo.SkuLockVO;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author eternity
 * @create 2019-11-17 15:26
 */
@Component
public class WareListener {

    @Autowired
    private WareSkuDao wareSkuDao;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @RabbitListener(queues = {"WMS-DEAD-QUEUE"})//监听队列
    public void unlock(String orderToken){
        //获取要解锁的所有库存
        String stockJson = this.stringRedisTemplate.opsForValue().get("order:stock:" + orderToken);
       //反序列化
        List<SkuLockVO> skuLockVOS = JSON.parseArray(stockJson, SkuLockVO.class);

        skuLockVOS.forEach(skuLockVO -> {//遍历解锁
            this.wareSkuDao.unLock(skuLockVO.getSkuWareId(),skuLockVO.getCount());
        });
    }

}
