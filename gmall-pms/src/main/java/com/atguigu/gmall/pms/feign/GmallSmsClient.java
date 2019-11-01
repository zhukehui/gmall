package com.atguigu.gmall.pms.feign;

import com.atguigu.core.bean.Resp;
import com.atguigu.gmall.sms.api.GmallSmsApi;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @author eternity
 * @create 2019-10-31 15:50
 */
@FeignClient("sms-service")
public interface GmallSmsClient extends GmallSmsApi {

}
