package com.atguigu.gmall.order.config;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.atguigu.gmall.order.vo.PayVo;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

//@ConfigurationProperties(prefix = "alipay")
@Component
@Data
public class AlipayTemplate {

    //在支付宝创建的应用的id
    private   String app_id = "2016101300674088";

    // 商户私钥，您的PKCS8格式RSA2私钥
    private  String merchant_private_key = "MIIEvwIBADANBgkqhkiG9w0BAQEFAASCBKkwggSlAgEAAoIBAQCWVW5ZXQUI9GVylSgLrS4r9kkZHfB+wBgibu/eCDbrfzz8ChKKszgp3sQqE/63Jht5gj+mEAUpLBid8Ygp+PfB3vdDELlqCGguEjJKJqLZOcpQIj8rBq+u5SbQ3P5aEiA8sY10JQJpzHNLgb/LoziUjMMIIsocSru0eCeu8fatqhYDXae5lvhDxnTcoF8RVhRqg5qt11uTxFOgyvBDhD1XXFZauTsYZreU9YVf7ftRjNqA3xbzoxXQLnL7ef7StoVKLqpAwXXolv+mpWUJY8U8KyhXHWsrJR7kBOOB0C00hMqe8RoVyy4RKgNOZSZjT/9vQRxm4FOwSim6r1TMbvTlAgMBAAECggEAfxCMaanu1IfFq1iY9W2WJgBP/pPIr0lr3MbJpXBqAxiTkjBtRaB/qxwnz60A0Qq7lDNb2t+VjRxBXlZFEbzmpI3xjlT3csDSVYZ5zSTk5hgrgRnBLHvFSF1VNppjvfuuFb644d9aL9Rn6v0rAFLgQubK/fhxvgIIr1wTppHLPzLFIHvIM03oua1IXGrWCSa1wsZe0SSdNX0dqJK16I3odiINaXBc/hjN5A1cYYQCxcbGHVepM60t4Rq+hHWfmVux6tHDx8VmYFzeq0WJaXYTatDQcQg+fmnAnQ4NpvUowJnPqM294o/tf9p/sqzDAyK1rmek4TSWuMYmz8I5yA6mDQKBgQDIHdNcHYSQr6c+XY/mF8vfKw7lSQh93iZo2xQpqAedx8pc7fId2yv+9esAArWZatmwKXK+P+Q1X58l+sADtI6r4yffMfdN1gfVKIAJcOocHHR42zkT8avzI3D741f+t+EWSIYz0XqugsiVtidRV34mGA5JAuWajjW2x9dHpGNoLwKBgQDAUKwSJl2m790dnY5l0GG7rS3yGYfdLXAFIopIPWcXbwXoRa0WozRHYXjVrqGUsCUXTz1TGWrXTAhyTowzKb/sfKlOZ1GYHPhrx8U8eDu1rlnpbxZQwFeSg0BuowI38+6mhWAA3MBpq251T9RTBSXEdvTFcFlakzl+EFQ/aNibKwKBgQC3TprNRjyoTD5mYk3t9t5JDqgWX0/n/BYy9nsBrWlMnjY/6bZeouUqJxmcaN7wKXA+bbktjFj+ixi9MXOx8TK/QNTFHn/qTKPgHbkq7MOF/Iq0KhueqUFY9p4IHbf9G3KTdtt3m+tE74fNm41odTyYqHBtcLnhWWLaizQn6+0jAQKBgQCFKlc76h0HLyhpDWRA094Y2n3qGgud94yNwbEG/J2Z+GKax0RnXHMiyjelntp73syQK2pHVF5K3ncqz4D9HHetBVUm2RwCvGy4FQhaKtcNosDXhvhoygbZiNDgyzcKD3q9C7XCYxlJEk/WI4TeR74zIDu1S2pT8YXp6NytW6hBKQKBgQCcQCD/AcS81OBdPg5nATmaAhvuXtpDs+KI42zttJIs/q/A2cQhHn7/fYV77F4g5HdPV+bX34wUbzxVggPm8ztOd+h9gyjRE8ZgpHJY/Yaq2SSjTUb0kPKwZobgLaPvrzMmWB6XiONkwneLoK7fL/Pvyo7NhokVaQ11tXOOLQLLig==";
    // 支付宝公钥,查看地址：https://openhome.alipay.com/platform/keyManage.htm 对应APPID下的支付宝公钥。
    private  String alipay_public_key = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA6LohrHufcLKINqWgB3p2sj9DOnQJWQ27b6dWIJp47aJYoJ6GpIXkkttZiLMeY2KdGuUwU0OhutFjnCT2KDliTsoVuRzfxdHtflGrNDmLlMaIXrrRqEHQLjwSjVR/14hs9hF8Lt6wMNTtLVWzzgsLFczB1t/pC8YKso2ozuzfuZadTc2Rw1hff8KiPXjbvt777PO716hAhBsX4v3Vlb8nYOu6LfyXjkwsSlr1BM99c6Jz+ywErl1w3h9Xoe/UrZXJmjTlLhc+AF0hEyIBi/GzkIuHX64zAmUI8xenBuKjJU1m26kxr6rQqWASAz1UlKMRZYT9p/Py5CV7W+pakTW9LQIDAQAB";
    // 服务器[异步通知]页面路径  需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
    // 支付宝会悄悄的给我们发送一个请求，告诉我们支付成功的信息
    private  String notify_url = "http://opkubt88kp.52http.net/api/order/pay/success";

    // 页面跳转同步通知页面路径 需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
    //同步通知，支付成功，一般跳转到成功页
    private  String return_url=null;

    // 签名方式
    private  String sign_type = "RSA2";

    // 字符编码格式
    private  String charset = "utf-8";

    // 支付宝网关； https://openapi.alipaydev.com/gateway.do
    private  String gatewayUrl = "https://openapi.alipaydev.com/gateway.do";

    public  String pay(PayVo vo) throws AlipayApiException {

        //AlipayClient alipayClient = new DefaultAlipayClient(AlipayTemplate.gatewayUrl, AlipayTemplate.app_id, AlipayTemplate.merchant_private_key, "json", AlipayTemplate.charset, AlipayTemplate.alipay_public_key, AlipayTemplate.sign_type);
        //1、根据支付宝的配置生成一个支付客户端
        AlipayClient alipayClient = new DefaultAlipayClient(gatewayUrl,
                app_id, merchant_private_key, "json",
                charset, alipay_public_key, sign_type);

        //2、创建一个支付请求 //设置请求参数
        AlipayTradePagePayRequest alipayRequest = new AlipayTradePagePayRequest();
        alipayRequest.setReturnUrl(return_url);
        alipayRequest.setNotifyUrl(notify_url);

        //商户订单号，商户网站订单系统中唯一订单号，必填
        String out_trade_no = vo.getOut_trade_no();
        //付款金额，必填
        String total_amount = vo.getTotal_amount();
        //订单名称，必填
        String subject = vo.getSubject();
        //商品描述，可空
        String body = vo.getBody();

        alipayRequest.setBizContent("{\"out_trade_no\":\""+ out_trade_no +"\","
                + "\"total_amount\":\""+ total_amount +"\","
                + "\"subject\":\""+ subject +"\","
                + "\"body\":\""+ body +"\","
                + "\"product_code\":\"FAST_INSTANT_TRADE_PAY\"}");

        String result = alipayClient.pageExecute(alipayRequest).getBody();

        //会收到支付宝的响应，响应的是一个页面，只要浏览器显示这个页面，就会自动来到支付宝的收银台页面
        System.out.println("支付宝的响应："+result);

        return result;

    }
}
