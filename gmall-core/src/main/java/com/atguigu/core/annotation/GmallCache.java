package com.atguigu.core.annotation;

import org.springframework.transaction.annotation.Transactional;

import java.lang.annotation.*;

/**
 * @author eternity
 * @create 2019-11-09 19:39
 */
@Target({ElementType.METHOD, ElementType.TYPE}) //指定可以作用在什么上
@Retention(RetentionPolicy.RUNTIME)  //指定是什么时注解 （指定为运行时）
//@Inherited  //是否可继承
@Documented  //要不要加入文档
public @interface GmallCache {

    /**
     * 缓存key的前缀默认
     * @return
     */
    String value() default "cache";

    /**
     * 默认过期时间,单位是秒
     * @return
     */
    long timeout() default 300L;

    /**
     * 为了防止缓存雪崩，而设置的过期时间的随机值范围
     * @return
     */
    long random() default 300L;

}
