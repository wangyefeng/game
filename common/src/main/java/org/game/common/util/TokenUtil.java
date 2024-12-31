package org.game.common.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;

import javax.management.timer.Timer;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class TokenUtil {
    //设置过期时间
    private static final long EXPIRE_TIME = 5 * Timer.ONE_MINUTE;

    //token秘钥
    private static final String TOKEN_SECRET = "HRTehj^$39Mghdkl%$%38";

    public static String token(int userId) {
        String token = "";
        try {
            //过期时间
            Date date = new Date(System.currentTimeMillis() + EXPIRE_TIME);
            //秘钥及加密算法
            Algorithm algorithm = Algorithm.HMAC256(TOKEN_SECRET);
            //设置头部信息
            Map<String, Object> header = new HashMap<>();
            header.put("typ", "JWT");
            header.put("alg", "HS256");
            //携带username，password信息，生成签名
            token = JWT.create()
                    .withHeader(header)
                    .withClaim("userId", userId)
                    .withExpiresAt(date)
                    .sign(algorithm);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return token;
    }

    public static boolean verify(String token, int userId) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(TOKEN_SECRET);
            JWTVerifier verifier = JWT.require(algorithm).build();
            DecodedJWT decodedJWT = verifier.verify(token);
            return decodedJWT.getClaim("userId").asInt().equals(userId);
        } catch (Exception e) {
            return false;
        }
    }
}