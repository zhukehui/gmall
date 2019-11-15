package com.atguigu.gmall.order.interceptor;

import com.atguigu.core.bean.UserInfo;
import com.atguigu.core.utils.CookieUtils;
import com.atguigu.core.utils.JwtUtils;
import com.atguigu.gmall.order.config.JwtProperties;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.UUID;

/**
 * @author eternity
 * @create 2019-11-13 16:36
 */

@Component
@EnableConfigurationProperties({JwtProperties.class})
public class LoginInterceptor extends HandlerInterceptorAdapter {

    @Autowired
    private JwtProperties jwtProperties;

    private static final ThreadLocal<UserInfo> THREAD_LOCAL = new ThreadLocal<>();


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        UserInfo userInfo = new UserInfo();

        //获取cookie信息(GMALL_TOKEN,UserKey)
        String token = CookieUtils.getCookieValue(request, this.jwtProperties.getCookieName());


        if (StringUtils.isEmpty(token)){
            return false;//拦截
        }

        // token不为空,解析token
        try {
            //解析gmall_token
            Map<String, Object> userInfoMap = JwtUtils.getInfoFromToken(token, this.jwtProperties.getPublicKey());

//        request.setAttribute("userId",userInfoMap.get("id").toString());
            userInfo.setUserId(Long.valueOf(userInfoMap.get("id").toString()));
        } catch (Exception e) {

            e.printStackTrace();
            return false;//拦截
        }
        // 保存到threadlocal
        THREAD_LOCAL.set(userInfo);

        return true;//放行
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

        //因为使用的是Tomcat线程池，请求结束不代表线程结束，所以要释放资源
        THREAD_LOCAL.remove();
    }


    public static UserInfo get(){
        return THREAD_LOCAL.get();
    }
}
