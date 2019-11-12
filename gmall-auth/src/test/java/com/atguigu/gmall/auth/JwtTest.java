package com.atguigu.gmall.auth;

import com.atguigu.core.utils.JwtUtils;
import com.atguigu.core.utils.RsaUtils;
import org.junit.Before;
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.HashMap;
import java.util.Map;

@SpringBootTest
public class JwtTest {
	private static final String pubKeyPath = "E:\\developWorkspace\\workspace_idea\\tmp\\rsa.pub";

    private static final String priKeyPath = "E:\\developWorkspace\\workspace_idea\\tmp\\rsa.pri";

    private PublicKey publicKey;

    private PrivateKey privateKey;

    @Test
    public void testRsa() throws Exception {
        RsaUtils.generateKey(pubKeyPath, priKeyPath, "zkh199831@$");
    }

    @Before
    public void testGetRsa() throws Exception {
        this.publicKey = RsaUtils.getPublicKey(pubKeyPath);
        this.privateKey = RsaUtils.getPrivateKey(priKeyPath);
    }

    @Test
    public void testGenerateToken() throws Exception {
        Map<String, Object> map = new HashMap<>();
        map.put("id", "11");
        map.put("username", "liuyan");
        // 生成token
        String token = JwtUtils.generateToken(map, privateKey, 5);
        System.out.println("token = " + token);
    }

    @Test
    public void testParseToken() throws Exception {
        String token = "eyJhbGciOiJSUzI1NiJ9.eyJpZCI6IjExIiwidXNlcm5hbWUiOiJsaXV5YW4iLCJleHAiOjE1NzM1NTQyNzJ9.XTKhTZpDBvb7nS8oBGzr_OZUxhGE2llXJ8rO9_V00vO8Ymh7kEBY9GxkqNXgo7w2IaA8jdS2Szsp01OL5gV4bz4ldIMLVyRjB06kPArXjiACJCwy7bVZYNMG2U7rioWKXqpOiFOF9SQtLjHnbv9id3UtlUxzeNQXKNz8ZdQ5El35283_kc8UQ4wL2Y9oSxkIK0G2LiYNIrBAhJJ4X75jfTr4NAJetUhAmZEAjCMi77Yfd1B4lP1yGou3vgHD5cHgIe05x_GY-8MrVVr_N5TdaLMOuWVoC4MP9X7yRH-hz8h1JegQ7eaBmgUMblFfBH9h2C98VCd4fDPGXh0LighBRg";

        // 解析token
        Map<String, Object> map = JwtUtils.getInfoFromToken(token, publicKey);
        System.out.println("id: " + map.get("id"));
        System.out.println("userName: " + map.get("username"));
    }
}