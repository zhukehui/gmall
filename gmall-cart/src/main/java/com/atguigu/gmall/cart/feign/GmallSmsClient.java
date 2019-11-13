package com.atguigu.gmall.cart.feign;

import com.atguigu.gmall.sms.api.GmallSmsApi;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @author eternity
 * @create 2019-11-13 18:21
 */
@FeignClient("sms-service")
public interface GmallSmsClient extends GmallSmsApi {

}
