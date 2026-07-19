package com.elsoldorado.app.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {
    @Value("${app.jwt.secret}")
    private String secretKey;

    @Value("${app.jwt.expiration-ms:86400000}")
    private long expirationTime;

    public String generarToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("roles", userDetails.getAuthorities().stream().map(a -> a.getAuthority()).toList());
        return generarToken(claims, userDetails);
    }

    public String generarToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        long now = System.currentTimeMillis();
        return Jwts.builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(now))
                .setExpiration(new Date(now + expirationTime))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean esTokenValido(String token, UserDetails userDetails) {
        String username = extraerUsername(token);
        return username != null && username.equals(userDetails.getUsername()) && userDetails.isEnabled() && !esTokenExpirado(token);
    }

    public String extraerUsername(String token) { return extraerClaim(token, Claims::getSubject); }
    public Date extraerExpiracion(String token) { return extraerClaim(token, Claims::getExpiration); }

    public <T> T extraerClaim(String token, Function<Claims, T> resolver) {
        return resolver.apply(extraerTodosLosClaims(token));
    }

    private boolean esTokenExpirado(String token) { return extraerExpiracion(token).before(new Date()); }

    private Claims extraerTodosLosClaims(String token) {
        return Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(token).getBody();
    }

    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
    }
}
