package com.proxyproject.shop.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.time.*;
import java.util.Date;
import java.util.List;

@Component
public class JwtService {
  @Value("${app.jwt.secret}") private String secret;
  @Value("${app.jwt.exp-minutes:60}") private long expMinutes;

  public String generate(String username, List<String> roles) {
    Instant now = Instant.now();
    return Jwts.builder()
      .setSubject(username)
      .claim("roles", roles)
      .setIssuedAt(Date.from(now))
      .setExpiration(Date.from(now.plus(Duration.ofMinutes(expMinutes))))
      .signWith(Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8)))
      .compact();
  }

  public Jws<Claims> parse(String token) {
    return Jwts.parserBuilder()
      .setSigningKey(Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8)))
      .build()
      .parseClaimsJws(token);
  }
}
