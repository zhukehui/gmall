package com.atguigu.gmall.search.feign;

import com.atguigu.gmall.wms.api.GmallWmsApi;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @author eternity
 * @create 2019-11-05 0:26
 */
@FeignClient("wms-service")
public interface GmallWmsClient extends GmallWmsApi {
}
