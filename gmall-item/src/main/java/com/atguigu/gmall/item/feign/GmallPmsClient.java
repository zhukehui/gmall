package com.atguigu.gmall.item.feign;

import com.atguigu.gmall.pms.api.GmallPmsApi;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @author eternity
 * @create 2019-11-10 12:53
 */
@FeignClient("pms-service")
public interface GmallPmsClient extends GmallPmsApi {
}
