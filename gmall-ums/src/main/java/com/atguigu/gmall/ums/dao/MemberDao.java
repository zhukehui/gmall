package com.atguigu.gmall.ums.dao;

import com.atguigu.gmall.ums.entity.MemberEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 会员
 * 
 * @author huigege
 * @email 574059694@qq.com
 * @date 2019-10-28 23:10:10
 */
@Mapper
public interface MemberDao extends BaseMapper<MemberEntity> {
	
}
