package org.wyf.game.common.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator.Builder;
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
            //设置头部信息
            Map<String, Object> header = new HashMap<>();
            header.put("typ", "JWT");
            header.put("alg", "HS256");
            //携带claims信息，生成签名
            Builder builder = JWT.create().withHeader(header);
            builder.withClaim("playerId", playerId);
            return builder.withExpiresAt(expiresAt).withIssuedAt(new Date()).sign(secret);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static DecodedJWT verify(String token, Algorithm secret) {
        try {
            JWTVerifier verifier = JWT.require(secret).build();
            return verifier.verify(token);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}