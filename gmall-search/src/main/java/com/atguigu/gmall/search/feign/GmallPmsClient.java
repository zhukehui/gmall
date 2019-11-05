package com.atguigu.gmall.search.feign;

import com.atguigu.gmall.pms.api.GmallPmsApi;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @author eternity
 * @create 2019-11-05 0:24
 */
@FeignClient("pms-service")
public interface GmallPmsClient extends GmallPmsApi {
}
