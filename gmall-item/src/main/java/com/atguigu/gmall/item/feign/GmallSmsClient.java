package com.atguigu.gmall.item.feign;

import com.atguigu.gmall.pms.api.GmallPmsApi;
import com.atguigu.gmall.sms.api.GmallSmsApi;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @author eternity
 * @create 2019-11-10 12:53
 */
@FeignClient("sms-service")
public interface GmallSmsClient extends GmallSmsApi {
}
