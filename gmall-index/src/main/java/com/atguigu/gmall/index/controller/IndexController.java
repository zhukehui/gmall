package com.atguigu.gmall.index.controller;

import com.atguigu.core.bean.Resp;
import com.atguigu.gmall.index.feign.GmallPmsClient;
import com.atguigu.gmall.index.service.IndexService;
import com.atguigu.gmall.pms.entity.CategoryEntity;
import com.atguigu.gmall.pms.vo.CategoryVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author eternity
 * @create 2019-11-08 17:56
 */
@RestController
@RequestMapping("/index")
public class IndexController {

    @Autowired
    private IndexService indexService;

    @GetMapping("/cates")
    public Resp<List<CategoryEntity>> queryLevellCategory(){
        List<CategoryEntity> categoryEntities = this.indexService.queryLevellCategory();

        return Resp.ok(categoryEntities);
    }

    @GetMapping("/cates/{pid}")
    public Resp<List<CategoryVO>> queryCategoryVO(@PathVariable("pid")Long pid){
        List<CategoryVO> categoryVOS = this.indexService.queryCategoryVO(pid);

        return Resp.ok(categoryVOS);
    }

    @GetMapping("/testLock")
    public Resp<Object> testLock(HttpServletRequest request){

        System.out.println(request.getLocalPort());

        String msg = this.indexService.testLock();

        return Resp.ok(msg);
    }
    @GetMapping("read")
    public Resp<Object> testRead(){
        String msg = this.indexService.testRead();
        return Resp.ok(msg);
    }

    @GetMapping("write")
    public Resp<Object> testWrite(){
        String msg = this.indexService.testWrite();
        return Resp.ok(msg);
    }

    @GetMapping("latch")
    public Resp<Object> testLatch() throws InterruptedException {
        String msg = this.indexService.testLatch();
        return Resp.ok(msg);
    }

    @GetMapping("out")
    public Resp<Object> testOut(){
        String msg = this.indexService.testOut();
        return Resp.ok(msg);
    }
}
