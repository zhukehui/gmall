package com.atguigu.gmall.cart.feign;

import com.atguigu.gmall.pms.api.GmallPmsApi;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @author eternity
 * @create 2019-11-13 18:21
 */
@FeignClient("pms-service")
public interface GmallPmsClient extends GmallPmsApi {

}
