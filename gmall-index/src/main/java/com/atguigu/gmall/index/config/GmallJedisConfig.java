package com.atguigu.gmall.index.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.JedisPool;

/**
 * @author eternity
 * @create 2019-11-08 22:40
 */
@Configuration
public class GmallJedisConfig {

    @Bean
    public JedisPool jedisPool(){

        return new JedisPool("192.168.228.188",6379);
    }
}
