package com.atguigu.gmall.pms.dao;

import com.atguigu.gmall.pms.entity.ProductAttrValueEntity;
import com.atguigu.gmall.pms.vo.SpuAttributeValueVO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * spu属性值
 * 
 * @author huigege
 * @email 574059694@qq.com
 * @date 2019-10-28 22:43:40
 */
@Mapper
public interface ProductAttrValueDao extends BaseMapper<ProductAttrValueEntity> {

    List<ProductAttrValueEntity> querySearchAttrValue(@Param("spuId") Long spuId);

    List<ProductAttrValueEntity> queryByGidAndSpuId(@Param("spuId") Long spuId,@Param("groupId") Long groupId);
}
