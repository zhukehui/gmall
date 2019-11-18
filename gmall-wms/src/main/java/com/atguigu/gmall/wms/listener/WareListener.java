package com.atguigu.gmall.wms.listener;

import com.alibaba.fastjson.JSON;
import com.atguigu.gmall.wms.dao.WareSkuDao;
import com.atguigu.gmall.wms.vo.SkuLockVO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
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

        if (StringUtils.isEmpty(stockJson)){
            return;
        }
        //反序列化
        List<SkuLockVO> skuLockVOS = JSON.parseArray(stockJson, SkuLockVO.class);

        skuLockVOS.forEach(skuLockVO -> {//遍历解锁库存
            this.wareSkuDao.unLock(skuLockVO.getSkuWareId(),skuLockVO.getCount());
        });

        this.stringRedisTemplate.delete("order:stock:" + orderToken);
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "WMS-STOCK-QUEUE", durable = "true"),
            exchange = @Exchange(value = "WMS-EXCHANGE", ignoreDeclarationExceptions = "true", type = ExchangeTypes.TOPIC),
            key = {"stock.minus"}
    ))
    public void minusStock(String orderToken){

        String stockJson = this.stringRedisTemplate.opsForValue().get("order:stock:" + orderToken);

        if (StringUtils.isEmpty(stockJson)){
            return;
        }
        //反序列化
        List<SkuLockVO> skuLockVOS = JSON.parseArray(stockJson, SkuLockVO.class);

        skuLockVOS.forEach(skuLockVO -> {//遍历解锁库存
            this.wareSkuDao.minus(skuLockVO.getSkuWareId(),skuLockVO.getCount());
        });
        this.stringRedisTemplate.delete("order:stock:" + orderToken);
    }

}
