package com.atguigu.gmall.index.service;

import com.atguigu.gmall.pms.entity.CategoryEntity;
import com.atguigu.gmall.pms.vo.CategoryVO;

import java.util.List;

/**
 * @author eternity
 * @create 2019-11-08 17:59
 */
public interface IndexService {
    List<CategoryEntity> queryLevellCategory();

    List<CategoryVO> queryCategoryVO(Long pid);

    String testLock();

    String testRead();

    String testWrite();

    String testLatch() throws InterruptedException;

    String testOut();
}
