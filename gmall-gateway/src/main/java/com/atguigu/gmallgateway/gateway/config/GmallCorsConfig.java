package com.atguigu.gmallgateway.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

/**
 * @author eternity
 * @create 2019-10-30 1:56
 */

@Configuration
public class GmallCorsConfig {

    @Bean
    public CorsWebFilter corsWebFilter(){

        //cors（跨域资源共享）配置对象
         CorsConfiguration configuration = new CorsConfiguration();
        configuration.addAllowedOrigin("http://localhost:1000");//允许哪些域名跨域请求
        configuration.addAllowedHeader("*");//允许跨域请求携带头信息
        configuration.addAllowedMethod("*");//允许跨域请求的方法
        configuration.setAllowCredentials(true);//是否允许携带cookie信息

        //cors（跨域资源共享）配置源
        UrlBasedCorsConfigurationSource configurationSource = new UrlBasedCorsConfigurationSource();
        configurationSource.registerCorsConfiguration("/**",configuration); // 添加映射路径，拦截一切请求

        return new CorsWebFilter(configurationSource);
    }
}
