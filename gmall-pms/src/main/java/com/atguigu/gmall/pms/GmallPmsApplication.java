package com.atguigu.gmall.pms;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * 在类中获取代理对象分三个步骤：
 *
 * 1. 导入aop的场景依赖：spring-boot-starter-aop
 * 2. 开启AspectJ的自动代理，同时要暴露代理对象：@EnableAspectJAutoProxy(exposeProxy=true)
 * 3. 获取代理对象：SpuInfoService proxy = (SpuInfoService) AopContext.currentProxy();
 */
@SpringBootApplication
@EnableSwagger2
@EnableDiscoveryClient
@MapperScan(basePackages = "com.atguigu.gmall.pms.dao")
@EnableFeignClients
@EnableAspectJAutoProxy(exposeProxy = true)
public class GmallPmsApplication {

    public static void main(String[] args) {
        SpringApplication.run(GmallPmsApplication.class, args);
    }

}
