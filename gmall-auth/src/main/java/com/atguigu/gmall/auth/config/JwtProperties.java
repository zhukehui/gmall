package com.atguigu.gmall.auth.config;

import com.atguigu.core.utils.RsaUtils;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.io.File;
import java.security.PrivateKey;
import java.security.PublicKey;

/**
 * @author eternity
 * @create 2019-11-12 18:54
 */
@ConfigurationProperties(prefix = "auth.jwt") //指定配置文件的前缀
@Data
@Slf4j
public class JwtProperties {//读取配置文件的类

    /**
     * auth:
     *   jwt:
     *     publicKeyPath: E:\developWorkspace\workspace_idea\tmp\rsa.pub  #公钥路径
     *     privateKeyPath: E:\developWorkspace\workspace_idea\tmp\rsa.pri  #私钥路径
     *     expire: 180  #过期时间，单位是分钟
     *     cookieName: GMALL_TOKEN  # cookie的名称
     *     secret: zkh199831@$%!  盐
     */
    private String publicKeyPath;

    private String privateKeyPath;

    private Integer expire;

    private String cookieName;

    private String secret;

    private PublicKey publicKey;

    private PrivateKey privateKey;


    /**
     * @PostConstruct加在方法上，指定bean对象创建好之后，调用该方法初始化对象，类似于xml的init-method方法。
     * @PreDestory加在方法上，指定bean销毁之前，调用该方法，类似于xml的destory-method方法。
     */
    @PostConstruct
    public void init(){
        try {
            //1.初始化公钥私钥文件
            File publicFile = new File(publicKeyPath);
            File privateFile = new File(privateKeyPath);

            //2.检查文件对象是否为空
            if (!publicFile.exists() || !privateFile.exists()){
                RsaUtils.generateKey(publicKeyPath ,privateKeyPath,secret );
            }

            //3.读取秘钥
            publicKey = RsaUtils.getPublicKey(publicKeyPath);
            privateKey = RsaUtils.getPrivateKey(privateKeyPath);
        } catch (Exception e) {
            log.error("初始化公钥或者私钥失败！",e);
            e.printStackTrace();
        }
    }
}
