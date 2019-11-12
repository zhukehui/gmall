package com.atguigu.gmallgateway.gateway.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * @author eternity
 * @create 2019-11-12 20:07
 */
@Component
public class AuthGatewayFilterFactory extends AbstractGatewayFilterFactory {//过滤器工厂

    @Autowired
    private AuthGatewayFilter authGatewayFilter;

    @Override
    public GatewayFilter apply(Object config) {

        return authGatewayFilter;
    }
}
