package de.rgse.timecap.service;

import android.util.Base64;

import java.io.IOException;
import java.util.Date;
import java.util.UUID;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

public class JwtService {

    private static final SignatureAlgorithm ALGORITHM = SignatureAlgorithm.HS256;

    private final String key;
    private final int expire;

    public JwtService() throws IOException {
        String jwtExpire = TimecapProperties.readProperty("rest.jwt.expire");
        String jwtKey = TimecapProperties.readProperty("rest.jwt.key");

        expire = Integer.parseInt(jwtExpire);
        key = Base64.encodeToString(jwtKey.getBytes(), Base64.DEFAULT);
    }

    public String generateJwt(String userId) {
        Date date = new Date();
        return Jwts.builder()
                .setIssuer(userId)
                .claim("iat", date.getTime())
                .claim("exp", new Date(date.getTime() + expire).getTime())
                .setId(UUID.randomUUID().toString())
                .signWith(ALGORITHM, key).compact();
    }
}
