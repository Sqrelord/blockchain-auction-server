package edu.dhu.auction.web.util;

import com.alibaba.fastjson.JSON;
import edu.dhu.auction.web.bean.Account;
import io.jsonwebtoken.*;
import org.apache.tomcat.util.codec.binary.Base64;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Date;

public class JwtUtils {
    public static final String TOKEN_HEADER = "Token";

    private static final String KEY = "MjI0MWViMDUtMjUxMC00NTA4LTliZDQtMjAwNmMzNmJkZmNl";

    private static final String ACCOUNT_CLAIMS = "account";

    private static final SecretKey JWT_SECRET_KEY = generalKey();

    private static final JwtBuilder builder = Jwts.builder().signWith(SignatureAlgorithm.HS256, JWT_SECRET_KEY);

    private static final JwtParser parser = Jwts.parser().setSigningKey(JWT_SECRET_KEY);

    public static String generateToken(Object account) {
        Date start = new Date(System.currentTimeMillis());
        Date end = new Date(System.currentTimeMillis() + 24 * 60 * 60 * 1000);
        return builder
                .claim(ACCOUNT_CLAIMS, JSON.toJSONString(account))
                .setIssuedAt(start)
                .setExpiration(end)
                .compact();
    }

    public static Account getAccount(String token) {
        Claims claims = parser.parseClaimsJws(token).getBody();
        return JSON.parseObject((String) claims.get(ACCOUNT_CLAIMS), Account.class);
    }

    private static SecretKey generalKey() {
        byte[] encodedKey = Base64.decodeBase64(KEY);
        return new SecretKeySpec(encodedKey, 0, encodedKey.length, "AES");
    }
}
