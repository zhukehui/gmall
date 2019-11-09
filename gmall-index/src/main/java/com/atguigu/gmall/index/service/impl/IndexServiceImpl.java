package com.atguigu.gmall.index.service.impl;

import com.alibaba.fastjson.JSON;
import com.atguigu.core.annotation.GmallCache;
import com.atguigu.core.bean.Resp;
import com.atguigu.gmall.index.feign.GmallPmsClient;
import com.atguigu.gmall.index.service.IndexService;
import com.atguigu.gmall.pms.entity.CategoryEntity;
import com.atguigu.gmall.pms.vo.CategoryVO;
import org.apache.commons.lang.StringUtils;
import org.redisson.api.RCountDownLatch;
import org.redisson.api.RLock;
import org.redisson.api.RReadWriteLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @author eternity
 * @create 2019-11-08 18:00
 */
@Service
public class IndexServiceImpl implements IndexService {

    @Autowired
    private GmallPmsClient gmallPmsClient;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private JedisPool jedisPool;
    @Autowired
    private RedissonClient redissonClient;

    private static final String KEY_PREFIX = "index:category";


    @Override
    public List<CategoryEntity> queryLevellCategory() {

        Resp<List<CategoryEntity>> resp = this.gmallPmsClient.queryCategories(1, null);
        return resp.getData();
    }

    @Override                      // 过期时间         为了防止缓存雪崩，而设置的过期时间的随机值范围
    @GmallCache(value = KEY_PREFIX ,timeout = 300000L ,random = 50000L)
    public List<CategoryVO> queryCategoryVO(Long pid) {
        //1.查询缓存，缓存中有的话直接返回
       /* String cache = this.stringRedisTemplate.opsForValue().get(KEY_PREFIX + pid);
        if (StringUtils.isNotBlank(cache)){
            // 如果缓存中有，直接返回
            JSON.parseArray(cache,CategoryVO.class);
        }*/
        //2.如果缓存中没有，查询数据库
        Resp<List<CategoryVO>> listResp = this.gmallPmsClient.queryCategoryWithSub(pid);
        List<CategoryVO> categoryVOS = listResp.getData();

        //3.查询完成之后，放入缓存  （为防止雪崩，设置随机过期天数    （不管有没有查询到，都放入缓存，防止穿透
//        this.stringRedisTemplate.opsForValue().set(KEY_PREFIX + pid,JSON.toJSONString(categoryVOS),5 + (int) (Math.random() * 5),TimeUnit.DAYS);

        return categoryVOS;
    }

    @Override
    public String testLock() {

        RLock lock = redissonClient.getLock("lock");
        lock.lock();

        String numString = this.stringRedisTemplate.opsForValue().get("num");
            if (StringUtils.isBlank(numString)){
                // 没有该值return
                return null;
            }
            // 有值就转成成int
            int num = Integer.parseInt(numString);
            // 把redis中的num值+1
            this.stringRedisTemplate.opsForValue().set("num",String.valueOf(++num));

            lock.unlock();

        return "添加成功";
    }

    @Override
    public String testRead() {
        RReadWriteLock readWriteLock = this.redissonClient.getReadWriteLock("readWriteLock");

        readWriteLock.readLock().lock(10,TimeUnit.SECONDS);

        String msg = this.stringRedisTemplate.opsForValue().get("msg");

//        readWriteLock.readLock().unlock();

        return msg;
    }

    @Override
    public String testWrite() {
        RReadWriteLock readWriteLock = this.redissonClient.getReadWriteLock("readWriteLock");
        readWriteLock.writeLock().lock(10 , TimeUnit.SECONDS);

        String msg = UUID.randomUUID().toString();
        this.stringRedisTemplate.opsForValue().set("msg",msg);

//        readWriteLock.writeLock().unlock();

        return "数据写入成功"+ msg;
    }

    @Override
    public String testLatch() throws InterruptedException {

        RCountDownLatch latchDown = this.redissonClient.getCountDownLatch("latchDown");

//        String countString = this.stringRedisTemplate.opsForValue().get("count");
//        int count = Integer.parseInt(countString);

        latchDown.trySetCount(5);

        latchDown.await();

        return "出去了。。。。";
    }

    @Override
    public String testOut() {

        RCountDownLatch latchDown = this.redissonClient.getCountDownLatch("latchDown");

//        String countString = this.stringRedisTemplate.opsForValue().get("count");
//        int count = Integer.parseInt(countString);
//        this.stringRedisTemplate.opsForValue().set("count",String.valueOf(--count));

        latchDown.countDown();


        return "进来了吗？";
    }

    public String testLock1() {
        //所有请求竞争锁       // 从redis中获取锁,setnx
        String uuid = UUID.randomUUID().toString();
        Boolean lock = this.stringRedisTemplate.opsForValue().setIfAbsent("lock", uuid,10,TimeUnit.SECONDS);
        //获取到锁执行业务逻辑
        if (lock){
            // 查询redis中的num值
            String numString = this.stringRedisTemplate.opsForValue().get("num");
            if (StringUtils.isBlank(numString)){
                // 没有该值return
                return null;
            }
            // 有值就转成成int
            int num = Integer.parseInt(numString);
            // 把redis中的num值+1
            this.stringRedisTemplate.opsForValue().set("num",String.valueOf(++num));

            //释放锁(判断是否是自己的那把锁)
            /*if (StringUtils.equals(uuid,this.stringRedisTemplate.opsForValue().get("lock"))){
                this.stringRedisTemplate.delete("lock");
            }*/

            /*如果出现异常可以使用jedis
            String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
            this.stringRedisTemplate.execute(new DefaultRedisScript<>(script),
                    Arrays.asList("lock"),Arrays.asList(uuid));*/
            Jedis jedis =null;
            try {
                jedis = jedisPool.getResource();
                String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
                jedis.eval(script,Arrays.asList("lock"),Arrays.asList(uuid));
            } finally {
                if (jedis != null){
                    jedis.close();
                }
            }

        }else {
            //没有获取到锁的请求进行重试
            try {
                TimeUnit.SECONDS.sleep(1);
                testLock(); //每隔1秒钟回调一次，再次尝试获取锁
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return "添加成功";
    }
}

