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

    public JwtService() throws IOException {
        String jwtKey = TimecapProperties.readProperty("rest.jwt.key");
        key = Base64.encodeToString(jwtKey.getBytes(), Base64.DEFAULT);
    }

    public String generateJwt(String userId) {
        return Jwts.builder().setIssuer(userId).setIssuedAt(new Date()).setId(UUID.randomUUID().toString()).signWith(ALGORITHM, key).compact();
    }
}
