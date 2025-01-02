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

    // token过期时间
    public static final long TOKEN_EXPIRE_TIME = Timer.ONE_MINUTE;

    // token秘钥
    public static final Algorithm TOKEN_SECRET = Algorithm.HMAC256("HRTehj^$39Mghdkl%$%38");

    // token秘钥
    public static final Algorithm PLAYER_TOKEN_SECRET = Algorithm.HMAC256("365zb5t3e4vb65%$#2390nb");

    public static String token(int playerId, Algorithm secret, Date expiresAt) {
        String token = "";
        try {
            //过期时间
            //设置头部信息
            Map<String, Object> header = new HashMap<>();
            header.put("typ", "JWT");
            header.put("alg", "HS256");
            //携带username，password信息，生成签名
            token = JWT.create()
                    .withHeader(header)
                    .withClaim("playerId", playerId)
                    .withExpiresAt(expiresAt)
                    .sign(secret);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return token;
    }

    public static boolean verify(String token, int playerId, Algorithm secret) {
        try {
            JWTVerifier verifier = JWT.require(secret).build();
            DecodedJWT decodedJWT = verifier.verify(token);
            return decodedJWT.getClaim("playerId").asInt().equals(playerId);
        } catch (Exception e) {
            return false;
        }
    }
}