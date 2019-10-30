package com.atguigu.gmall.pms.VO;

import com.atguigu.gmall.pms.entity.AttrAttrgroupRelationEntity;
import com.atguigu.gmall.pms.entity.AttrEntity;
import com.atguigu.gmall.pms.entity.AttrGroupEntity;
import lombok.Data;

import java.util.List;

/**
 * @author eternity
 * @create 2019-10-31 0:54
 */
@Data
public class AttrGroupVO extends AttrGroupEntity {


    private List<AttrEntity> attrEntities;

    private List<AttrAttrgroupRelationEntity> relations;


}
