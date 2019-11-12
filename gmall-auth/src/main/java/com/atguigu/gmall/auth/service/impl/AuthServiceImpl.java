package com.atguigu.gmall.auth.service.impl;

import com.atguigu.core.bean.Resp;
import com.atguigu.core.exception.GmallException;
import com.atguigu.core.utils.JwtUtils;
import com.atguigu.gmall.auth.config.JwtProperties;
import com.atguigu.gmall.auth.feign.GmallUmsClient;
import com.atguigu.gmall.auth.service.AuthService;
import com.atguigu.gmall.ums.entity.MemberEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;

import java.security.PrivateKey;
import java.util.HashMap;
import java.util.Map;

/**
 * @author eternity
 * @create 2019-11-12 18:30
 */
@Service
@EnableConfigurationProperties({JwtProperties.class})
public class AuthServiceImpl implements AuthService {

    @Autowired
    private GmallUmsClient gmallUmsClient;

    @Autowired
    private JwtProperties jwtProperties;

    @Override
    public String accredit(String username, String password) {
        try {
            //1.远程调用用户中心的数据接口，查询用户信息
            Resp<MemberEntity> memberEntityResp = this.gmallUmsClient.queryUser(username, password);
            MemberEntity memberEntity = memberEntityResp.getData();

            //2、判断用户是否存在，不存在直接返回
            if (memberEntity == null){
                return null;
            }

            //3、存在，生成jwt
            Map<String,Object> map = new HashMap<>();
            map.put("id",memberEntity.getId());
            map.put("username",memberEntity.getUsername());
            return JwtUtils.generateToken(map, this.jwtProperties.getPrivateKey(), this.jwtProperties.getExpire());
        } catch (Exception e) {
            e.printStackTrace();
            throw new GmallException("jwt认证失败");
        }

    }
}
