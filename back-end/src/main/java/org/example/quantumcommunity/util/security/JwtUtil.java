package org.example.quantumcommunity.util.security;

import java.security.SignatureException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import io.jsonwebtoken.*;

/**
 * @author xiaol
 */
public class JwtUtil {
    private static final String SECRET_KEY="xiaoliangzi2003";

    public static String generateToken(String username, String password) {
        Date now = new Date();
        Map<String, Object> claims = new HashMap<>();
        claims.put("username", username);
        claims.put("password", password);
        claims.put("secret", SECRET_KEY);
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + 1000 * 60 * 60 * 24))
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                .compact();
    }

    public static Claims parseToken(String token) throws SignatureException {
        try{
            return Jwts.parser()
                    .setSigningKey(SECRET_KEY)
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e){
            throw new ExpiredJwtException(null,null,"Token expired");
        }
    }
}
