package com.atguigu.gmall.sms.api;

import com.atguigu.core.bean.Resp;
import com.atguigu.gmall.sms.vo.SaleVO;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @author eternity
 * @create 2019-11-01 19:33
 */
public interface GmallSmsApi {

    @PostMapping("sms/skubounds/sale")
    public Resp<Object> saveSale(@RequestBody SaleVO saleVO);
}
