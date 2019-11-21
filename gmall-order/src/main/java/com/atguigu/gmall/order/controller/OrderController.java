package com.atguigu.gmall.order.controller;

import com.alipay.api.AlipayApiException;
import com.atguigu.core.bean.Resp;
import com.atguigu.core.bean.UserInfo;
import com.atguigu.gmall.oms.entity.OrderEntity;
import com.atguigu.gmall.oms.vo.OrderSubmitVO;
import com.atguigu.gmall.order.config.AlipayTemplate;
import com.atguigu.gmall.order.interceptor.LoginInterceptor;
import com.atguigu.gmall.order.service.OrderService;
import com.atguigu.gmall.order.vo.OrderConfirmVO;
import com.atguigu.gmall.order.vo.PayAsyncVo;
import com.atguigu.gmall.order.vo.PayVo;
import com.atguigu.gmall.order.vo.SeckillVO;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RCountDownLatch;
import org.redisson.api.RSemaphore;
import org.redisson.api.RedissonClient;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

/**
 * @author eternity
 * @create 2019-11-15 20:10
 */
@RestController
@RequestMapping("order")
public class OrderController {

    @Autowired
    private OrderService orderService;
    @Autowired
    private AlipayTemplate alipayTemplate;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private RedissonClient redissonClient;
    @Autowired
    private AmqpTemplate amqpTemplate;

    @PostMapping("/submit")
    public  Resp<Object> submit(@RequestBody OrderSubmitVO orderSubmitVO){
        String form =null;
        try {
        OrderEntity orderEntity = this.orderService.submit(orderSubmitVO);

        PayVo payVo = new PayVo();
        payVo.setBody("谷粒商城支付系统");
        payVo.setSubject("支付平台");
        payVo.setTotal_amount(orderEntity.getTotalAmount().toString());//价格
        payVo.setOut_trade_no(orderEntity.getOrderSn());//防重

            form = this.alipayTemplate.pay(payVo);
            System.out.println(form);
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }

        return Resp.ok(form);
    }

    @GetMapping("/confirm")
    public Resp<OrderConfirmVO> confirm(){

        OrderConfirmVO orderConfirmVO = this.orderService.confirm();

        return Resp.ok(orderConfirmVO);
    }

    @PostMapping("/pay/success")
    public Resp<Object> paySuccess(PayAsyncVo payAsyncVo){

        System.out.println("***********支付成功***********");
        //订单状态的修改和库存的修改
        this.orderService.paySuccess(payAsyncVo.getOut_trade_no());
        return Resp.ok(null);
    }

    /**
     * 分布式并发工具类，快速的腾出服务器的资源来处理其他请求；
     * @param skuId
     * @return
     */
    @RequestMapping("seckill/{skuId}")
    public Resp<Object> seckill(@PathVariable("skuId") Long skuId) throws InterruptedException {

        // 查询库存
        String stockJson = this.stringRedisTemplate.opsForValue().get("seckill:stock:" + skuId);

        if (StringUtils.isEmpty(stockJson)){
            return Resp.ok("该秒杀已結束!");
        }

        Integer stock = Integer.valueOf(stockJson);

        // 通过信号量，获取秒杀库存
        RSemaphore semaphore = this.redissonClient.getSemaphore("seckill:lock:" + skuId);

        semaphore.trySetPermits(stock);
        semaphore.acquire(1);


        UserInfo userInfo = LoginInterceptor.get();

        RCountDownLatch countDownLatch = this.redissonClient.getCountDownLatch("seckill:count:" + userInfo.getUserId());

        countDownLatch.trySetCount(1);

        SeckillVO seckillVO = new SeckillVO();
        seckillVO.setSkuId(skuId);
        seckillVO.setUserId(userInfo.getUserId());
        seckillVO.setCount(1);

        this.amqpTemplate.convertAndSend("SECKILL-EXCHANGE","seckill.create",seckillVO);

        this.stringRedisTemplate.opsForValue().set("seckill:stock:" + skuId,String.valueOf(--stock));

        return Resp.ok(null);
    }

    @GetMapping
    public Resp<OrderEntity> queryOrder() throws InterruptedException {

        UserInfo userInfo = LoginInterceptor.get();

        RCountDownLatch countDownLatch = this.redissonClient.getCountDownLatch("seckill:count:" + userInfo.getUserId());
        countDownLatch.await();

        OrderEntity orderEntity = this.orderService.queryOrder();

        return Resp.ok(orderEntity);

    }

    /**
     * 分布式并发工具类，快速的腾出服务器的资源来处理其他请求；
     * @param skuId
     * @return
     */
/*    @GetMapping("/miaosha/{skuId}")
    public Resp<Object> kill(@PathVariable("skuId") Long skuId){
        Long userId = LoginInterceptor.getUserInfo().getId();
        if(userId!=null){
            // 查询库存
            String stock = this.redisTemplate.opsForValue().get("sec:stock:" + skuId);
            if (StringUtils.isEmpty(stock)){
                return Resp.fail("秒杀结束！");
            }

            // 通过信号量，获取秒杀库存
            RSemaphore semaphore = this.redissonClient.getSemaphore("sec:semaphore:" + skuId);
            semaphore.trySetPermits(Integer.valueOf(stock));
            //0.1s
            boolean b = semaphore.tryAcquire();
            if(b){
                //创建订单
                String orderSn = IdWorker.getTimeId();

                SkuLockVO lockVO = new SkuLockVO();
                lockVO.setOrderToken(orderSn);
                lockVO.setNum(1);
                lockVO.setSkuId(skuId);

                //准备闭锁信息
                RCountDownLatch latch = this.redissonClient.getCountDownLatch("sec:countdown:" + orderSn);
                latch.trySetCount(1);

                this.amqpTemplate.convertAndSend("SECKILL-ORDER", "sec.kill", lockVO);
                return Resp.ok("秒杀成功，订单号：" + orderSn);
            }else {
                return Resp.fail("秒杀失败，欢迎再次秒杀！");
            }
        }
        return Resp.fail("请登录后再试！");
    }

    @GetMapping("/miaosha/pay")
    public String payKillOrder(String orderSn) throws InterruptedException {

        RCountDownLatch latch = this.redissonClient.getCountDownLatch("sec:ok:" + orderSn);

        latch.await();

        // 查询订单信息

        return "";
    }*/

}
