package com.atguigu.gmall.auth.feign;

import com.atguigu.gmall.ums.api.GmallUmsApi;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @author eternity
 * @create 2019-11-12 18:44
 */
@FeignClient("ums-service")
public interface GmallUmsClient extends GmallUmsApi {
}
