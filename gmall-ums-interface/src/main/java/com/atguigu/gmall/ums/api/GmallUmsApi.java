package com.atguigu.gmall.ums.api;

import com.atguigu.core.bean.Resp;
import com.atguigu.gmall.ums.entity.MemberEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author eternity
 * @create 2019-11-12 18:42
 */
public interface GmallUmsApi {

    @GetMapping("ums/member/query")
    public Resp<MemberEntity> queryUser(@RequestParam("username")String username, @RequestParam("password")String password);

}
