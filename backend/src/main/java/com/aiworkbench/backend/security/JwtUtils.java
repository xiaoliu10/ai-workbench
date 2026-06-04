package com.aiworkbench.backend.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;

@Slf4j
@Component
public class JwtUtils {

	private final SecretKey key;
	private final long expiration;

	public JwtUtils(
			@Value("${jwt.secret}") String secret,
			@Value("${jwt.expiration}") long expiration
	) {
		this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
		this.expiration = expiration;
	}

	/**
	 * 生成 JWT Token
	 */
	public String generateToken(Long userId, String username, List<String> roles) {
		Date now = new Date();
		Date expiryDate = new Date(now.getTime() + expiration);

		return Jwts.builder()
				.subject(username)
				.claim("userId", userId)
				.claim("roles", roles)
				.issuedAt(now)
				.expiration(expiryDate)
				.signWith(key)
				.compact();
	}

	/**
	 * 从 Token 中解析 Claims
	 */
	public Claims parseToken(String token) {
		return Jwts.parser()
				.verifyWith(key)
				.build()
				.parseSignedClaims(token)
				.getPayload();
	}

	/**
	 * 从 Token 中获取用户名
	 */
	public String getUsername(String token) {
		return parseToken(token).getSubject();
	}

	/**
	 * 从 Token 中获取用户 ID
	 */
	public Long getUserId(String token) {
		return parseToken(token).get("userId", Long.class);
	}

	/**
	 * 验证 Token 是否有效
	 */
	public boolean validateToken(String token) {
		try {
			parseToken(token);
			return true;
		} catch (ExpiredJwtException e) {
			log.warn("JWT Token 已过期: {}", e.getMessage());
		} catch (JwtException e) {
			log.warn("JWT Token 无效: {}", e.getMessage());
		}
		return false;
	}
}
