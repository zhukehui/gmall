package com.atguigu.gmall.index.aspectj;

import com.alibaba.fastjson.JSON;
import com.atguigu.core.annotation.GmallCache;
import org.apache.commons.lang.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

/**
 * @author eternity
 * @create 2019-11-09 20:16
 */
@Aspect //声明为一个切面类
@Component
public class CacheAspectj {

    /**环绕通知：
     * 1.返回值object
     * 2.参数proceedingJoinPoint
     * 3.抛出异常Throwable
     * 4.proceedingJoinPoint.proceed(args)执行业务方法
     */
    //execution(* com.atguigu.gmall.index.service.impl.*.*(..))

    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private RedissonClient redissonClient;

    @Around("@annotation(com.atguigu.core.annotation.GmallCache)") //拦截使用该注解的方法
    public Object cacheAroundAdvice(ProceedingJoinPoint proceedingJoinPoint)throws Throwable{
        Object result =null;
        //获取注解
        MethodSignature signature = (MethodSignature) proceedingJoinPoint.getSignature();// 获取方法签名
        GmallCache annotation = signature.getMethod().getAnnotation(GmallCache.class);//获取注解对象
        Class returnType = signature.getReturnType();//获取方法的返回值类型

        String value = annotation.value();//获取缓存key的前缀


        String args = Arrays.asList(proceedingJoinPoint.getArgs()).toString();//方法的参数

        //查询缓存                 拼接上方法的参数：pid
        String key = value + ":" + args;
        result = cacheHit(key, returnType);
        if (result != null){
            return result;
        }

        //如果没有，加分布式锁                    拼接上方法参数，保证只锁请求的
        RLock lock = this.redissonClient.getLock("lock" + args);
        lock.lock();

        //查询缓存
        result = cacheHit(key, returnType);
        //如果缓存中有，直接返回 并且释放分布式锁
        if (result != null){
            lock.unlock();
            return result;
        }

        //执行查询的业务逻辑从数据库查询
        result = proceedingJoinPoint.proceed(proceedingJoinPoint.getArgs());

        //放入缓存  并且释放分布式锁
        long timeout = annotation.timeout();//获取过期时间
        timeout = timeout + (long)(Math.random() * annotation.random());
        this.stringRedisTemplate.opsForValue().set(key,JSON.toJSONString(result),timeout, TimeUnit.SECONDS);

        lock.unlock();

        return result;
    }

    private Object cacheHit(String key,Class returnType){



        String jsonString = this.stringRedisTemplate.opsForValue().get(key);

        //如果缓存中有，直接返回
        if (StringUtils.isNotBlank(jsonString)){
            return JSON.parseObject(jsonString,returnType);
        }
        return null;
    }
}
