package com.atguigu.gmall.auth.controller;

import com.atguigu.core.bean.Resp;
import com.atguigu.core.utils.CookieUtils;
import com.atguigu.gmall.auth.config.JwtProperties;
import com.atguigu.gmall.auth.service.AuthService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author eternity
 * @create 2019-11-12 18:28
 */
@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;
    @Autowired
    private JwtProperties jwtProperties;

    @PostMapping("/accredit")
    public Resp<Object> accredit(@RequestParam("username")String username, @RequestParam("password")String password,
                                 HttpServletRequest request, HttpServletResponse response){
        // 登录校验，查看是否携带token
        String jwtToken = this.authService.accredit(username, password);

        if (StringUtils.isEmpty(jwtToken)){
            return Resp.fail("登录失败，用户名或密码错误");
        }

        //4、把生成的jwt放入cookie中
        CookieUtils.setCookie(request,response,this.jwtProperties.getCookieName(),jwtToken,
                //设置的过期时间为分钟默认为秒
                this.jwtProperties.getExpire() * 60);


        return Resp.ok("登录成功");

    }

}
