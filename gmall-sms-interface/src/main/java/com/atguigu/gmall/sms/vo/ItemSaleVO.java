package com.atguigu.gmall.sms.vo;

import lombok.Data;

/**
 * @author eternity
 * @create 2019-11-09 22:13
 */
@Data
public class ItemSaleVO {

    private String type; //满减  打折  积分

    private String desc; //优惠信息的具体描述 促销信息/优惠券的名字
}
