package com.atguigu.gmall.ums.service.impl;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;
import java.util.UUID;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.core.bean.PageVo;
import com.atguigu.core.bean.Query;
import com.atguigu.core.bean.QueryCondition;

import com.atguigu.gmall.ums.dao.MemberDao;
import com.atguigu.gmall.ums.entity.MemberEntity;
import com.atguigu.gmall.ums.service.MemberService;
import org.springframework.web.bind.annotation.RequestParam;


@Service("memberService")
public class MemberServiceImpl extends ServiceImpl<MemberDao, MemberEntity> implements MemberService {

    @Override
    public PageVo queryPage(QueryCondition params) {
        IPage<MemberEntity> page = this.page(
                new Query<MemberEntity>().getPage(params),
                new QueryWrapper<MemberEntity>()
        );

        return new PageVo(page);
    }

    @Override
    public Boolean checkData(String data, Integer type) {
        QueryWrapper<MemberEntity> queryWrapper = new QueryWrapper<>();
        switch (type){
            case 1: queryWrapper.eq("username",data); break;
            case 2: queryWrapper.eq("mobile",data); break;
            case 3: queryWrapper.eq("email",data); break;
            default: return false;

        }


        return this.count(queryWrapper) == 0 ;
    }

    @Override
    public void register(MemberEntity memberEntity,String code) {

        //1.校检验证码

        //2.生成盐
        String salt = UUID.randomUUID().toString().substring(0, 6);
        memberEntity.setSalt(salt);

        //3.加盐加密
        memberEntity.setPassword(DigestUtils.md5Hex(memberEntity.getPassword() + salt));

        //4.注册功能
        memberEntity.setLevelId(1L);
        memberEntity.setStatus(1);
        memberEntity.setCreateTime(new Date());
        memberEntity.setIntegration(0);
        memberEntity.setGrowth(0);
        this.save(memberEntity);
        //5.删除Redis中的验证码
    }

    @Override
    public MemberEntity queryUser(String username, String password) {

        MemberEntity memberEntity = this.getOne(new QueryWrapper<MemberEntity>().eq("username", username));
        //如果根据用户名查询的用户不存在
        if (memberEntity ==null){

            throw new IllegalArgumentException("用户名或密码不合法!");

        }

        String salt = memberEntity.getSalt();//获取盐
        password = DigestUtils.md5Hex(password + salt);//对用户输入的密码进行加密
        if (!StringUtils.equals(password,memberEntity.getPassword())){

            throw new IllegalArgumentException("用户名或密码不合法!");

        }
        return memberEntity;
    }

}