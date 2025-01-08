package org.game.common.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class TokenUtil {

    // token秘钥
    public static final Algorithm PLAYER_TOKEN_SECRET = Algorithm.HMAC256("365zb5t3e4vb65%$#2390nb");

    public static String token(int playerId, Algorithm secret, Date expiresAt) {
        try {
            //过期时间
            //设置头部信息
            Map<String, Object> header = new HashMap<>();
            header.put("typ", "JWT");
            header.put("alg", "HS256");
            //携带username，password信息，生成签名
            return JWT.create()
                    .withHeader(header)
                    .withClaim("playerId", playerId)
                    .withExpiresAt(expiresAt)
                    .sign(secret);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
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