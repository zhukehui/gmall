package com.atguigu.gmall.pms.vo;

import com.atguigu.gmall.pms.entity.CategoryEntity;
import lombok.Data;

import java.util.List;

/**
 * @author eternity
 * @create 2019-11-08 18:39
 */
@Data
public class CategoryVO extends CategoryEntity {

    private List<CategoryEntity> subs;
}
