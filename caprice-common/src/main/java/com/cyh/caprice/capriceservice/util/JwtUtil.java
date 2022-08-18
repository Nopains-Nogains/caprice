package com.cyh.caprice.capriceservice.util;


import io.jsonwebtoken.*;
import org.apache.tomcat.util.codec.binary.Base64;

import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.Date;
import java.util.Map;

/**
 * @Author: cuiyuhao
 * @Description:
 * @Data: Created in 14:37 2021/10/12
 **/
public class JwtUtil {
    /**
     * Token过期时间必须大于生效时间
     */
    private static final Long TOKEN_EXPIRE_TIME = 3 * 60 * 60 * 1000L;
    /**
     * Token加密解密的密码
     */
    private static final String TOKEN_SECRET = "jsc";
    /**
     * 加密类型 三个值可取 HS256  HS384  HS512
     */
    private static final SignatureAlgorithm JWT_ALG = SignatureAlgorithm.HS256;
    /**
     * token生效时间(默认是从当前开始生效)
     * 默认：new Date(System.currentTimeMillis() + START_TIME)
     */
    private static final Long START_TIME = 0L;
    /**
     * token在什么时间之前是不可用的（默认从当前时间）
     * 默认：new Date(System.currentTimeMillis() + BEFORE_TIME)
     */
    private static final Long BEFORE_TIME = 0L;

    private static Key generateKey() {
        // 将将密码转换为字节数组
        byte[] bytes = Base64.decodeBase64(TOKEN_SECRET);
        // 根据指定的加密方式，生成密钥
        return new SecretKeySpec(bytes, JWT_ALG.getJcaName());
    }

    /**
     * 创建token
     *
     * @param sub token所面向的用户
     * @param aud 接收token的一方
     * @param jti token的唯一身份标识，主要用来作为一次性token,从而回避重放攻击
     * @param iss token签发者
     * @param map 自定义信息的存储
     * @return 加密后的token字符串
     */
    public static String createToken(String sub, String aud, String jti, String iss, Map<String, Object> map) {
        final JwtBuilder builder = Jwts.builder();
        if (!map.isEmpty()) {
            builder.setClaims(map);
        }
        String token = builder
                .signWith(JWT_ALG, generateKey())
                .setSubject(sub)
                .setAudience(aud)
                .setIssuer(iss)
                .setNotBefore(new Date(System.currentTimeMillis() + BEFORE_TIME))
                .setIssuedAt(new Date(System.currentTimeMillis() + START_TIME))
                .setExpiration(new Date(System.currentTimeMillis() + TOKEN_EXPIRE_TIME))
                .compact();
        return token;
    }

    /**
     * 创建token
     *
     * @param sub token所面向的用户
     * @param aud 接收token的一方
     * @param map 自定义信息存储
     * @return token 字符串
     */
    public static String createToken(String sub, String aud, Map<String, Object> map) {
        return createToken(sub, aud, DateUtils.getCurrentTime(), null, map);
    }

    /**
     * 创建token
     *
     * @param sub token所面向的用户
     * @param map 自定义信息存储
     * @return token字符串
     */
    public static String createToken(String sub, Map<String, Object> map) {
        return createToken(sub, null, map);
    }


    /**
     * 解析token
     * 可根据Jws<Claims>   获取  header|body|getSignature三部分数据
     *
     * @param token token字符串
     * @return Jws
     */
    public static Jws<Claims> parseToken(String token) {
        // 解析 token 字符串
        return Jwts.parser().setSigningKey(generateKey()).parseClaimsJws(token);
    }

    /**
     * 校验token,校验是否是本服务器的token
     *
     * @param token token字符串
     * @return boolean
     */
    public static Boolean checkToken(String token) {
        return parseToken(token).getBody() != null;
    }

    /**
     * 根据sub判断token
     *
     * @param token token字符串
     * @param sub   面向的用户
     * @return boolean
     */
    public static Boolean checkToken(String token, String sub) {
        return parseToken(token).getBody().getSubject().equals(sub);
    }
}
