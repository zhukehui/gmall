package com.atguigu.gmall.index.feign;

import com.atguigu.gmall.pms.api.GmallPmsApi;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @author eternity
 * @create 2019-11-08 17:54
 */
@FeignClient("pms-service")
public interface GmallPmsClient extends GmallPmsApi {
}
