package com.atguigu.gmall.oms.feign;

import com.atguigu.gmall.pms.api.GmallPmsApi;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @author eternity
 * @create 2019-11-16 20:43
 */
@FeignClient("pms-service")
public interface GmallPmsClient extends GmallPmsApi {
}
