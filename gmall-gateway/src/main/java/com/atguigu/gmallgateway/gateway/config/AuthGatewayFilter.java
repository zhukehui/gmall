package com.atguigu.gmallgateway.gateway.config;

import com.atguigu.core.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.core.Ordered;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.MultiValueMap;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.text.CollationKey;

/**
 * @author eternity
 * @create 2019-11-12 20:06
 */
@Component
@EnableConfigurationProperties({JwtProperties.class})
public class AuthGatewayFilter implements GatewayFilter, Ordered {//过滤器  Ordered:返回值越小过滤器优先级越高

    @Autowired
    private JwtProperties jwtProperties;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // 获取request和response，注意：不是HttpServletRequest及HttpServletResponse
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();
        //获取cookie中的token信息
        // 获取所有cookie
        MultiValueMap<String, HttpCookie> cookies = request.getCookies();

        //判断是否存在，不存在重定向到登录页面
        //如果cookies为空或者不包含指定的token，则相应认证未通过
        if (CollectionUtils.isEmpty(cookies) || !cookies.containsKey(this.jwtProperties.getCookieName())){

            response.setStatusCode(HttpStatus.UNAUTHORIZED); // 设置响应状态码为未认证！

            return response.setComplete();// 结束请求

        }

        //存在，解析
        HttpCookie cookie = cookies.getFirst(this.jwtProperties.getCookieName());
        try {
            JwtUtils.getInfoFromToken(cookie.getValue(),this.jwtProperties.getPublicKey());
        } catch (Exception e) {
            e.printStackTrace();
            //出现异常
            response.setStatusCode(HttpStatus.UNAUTHORIZED); // 设置响应状态码为未认证！

            return response.setComplete();// 结束请求
        }

        return chain.filter(exchange);//放行
    }

    @Override
    public int getOrder() {//返回值越小过滤器优先级越高
        return 0;
    }
}
