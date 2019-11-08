package com.atguigu.gmall.index.service.impl;

import com.alibaba.fastjson.JSON;
import com.atguigu.core.bean.Resp;
import com.atguigu.gmall.index.feign.GmallPmsClient;
import com.atguigu.gmall.index.service.IndexService;
import com.atguigu.gmall.pms.entity.CategoryEntity;
import com.atguigu.gmall.pms.vo.CategoryVO;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * @author eternity
 * @create 2019-11-08 18:00
 */
@Service
public class IndexServiceImpl implements IndexService {

    @Autowired
    private GmallPmsClient gmallPmsClient;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    private String KEY_PREFIX = "index:category:";
    @Override
    public List<CategoryEntity> queryLevellCategory() {

        Resp<List<CategoryEntity>> resp = this.gmallPmsClient.queryCategories(1, null);
        return resp.getData();
    }

    @Override
    public List<CategoryVO> queryCategoryVO(Long pid) {
        //1.查询缓存，缓存中有的话直接返回
        String cache = this.stringRedisTemplate.opsForValue().get(KEY_PREFIX + pid);
        if (StringUtils.isNotBlank(cache)){
            // 如果缓存中有，直接返回
            JSON.parseArray(cache,CategoryVO.class);
        }
        //2.如果缓存中没有，查询数据库
        Resp<List<CategoryVO>> listResp = this.gmallPmsClient.queryCategoryWithSub(pid);
        List<CategoryVO> categoryVOS = listResp.getData();

        //3.查询完成之后，放入缓存
        this.stringRedisTemplate.opsForValue().set(KEY_PREFIX + pid,JSON.toJSONString(categoryVOS));

        return categoryVOS;
    }

    @Override
    public synchronized String testLock() {
        // 查询redis中的num值
        String numString = this.stringRedisTemplate.opsForValue().get("num");
        if (StringUtils.isBlank(numString)){
            // 没有该值return
            return null;
        }
        // 有值就转成成int
        int num = Integer.parseInt(numString);
        // 把redis中的num值+1
        this.stringRedisTemplate.opsForValue().set("num",String.valueOf(++num));

        return "添加成功";
    }
}

