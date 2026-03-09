package com.project.inventory_management_system.utiliities;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Map;

import static com.project.inventory_management_system.constants.ConnectConstants.JWT_EXPIRATION;
import static com.project.inventory_management_system.constants.ConnectConstants.JWT_SECRET;

@Component
public class JwtUtil
{

    @Value(JWT_SECRET)
    private String secret;

    @Value(JWT_EXPIRATION)
    private long jwtExpiration;

    private SecretKey key;

    @PostConstruct
    public void init()
    {
        this.key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
    }


    public String extractUsername(String token)
    {
        Claims claims = extractAllClaims(token);
        return claims.getSubject();
    }

    public Date extractExpiration(String token)
    {
        return extractAllClaims(token).getExpiration();
    }

    public Claims extractAllClaims(String token)
    {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public Boolean isTokenExpired(String token)
    {
        return extractExpiration(token).before(new Date());
    }

    public Boolean isTokenValid(String token, String username)
    {
        final String extractedUsername = extractUsername(token);
        return (extractedUsername != null && extractedUsername.equals(username) && !isTokenExpired(token));
    }

    public String generateToken(Map<String, Object> claims, String subject)
    {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpiration))
                .signWith(key)
                .compact();
    }

}
