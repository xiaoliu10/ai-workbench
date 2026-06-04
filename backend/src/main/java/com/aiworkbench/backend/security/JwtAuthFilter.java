package com.aiworkbench.backend.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

	private final JwtUtils jwtUtils;
	private final UserDetailsService userDetailsService;

	private static final String AUTH_HEADER = "Authorization";
	private static final String BEARER_PREFIX = "Bearer ";

	@Override
	protected void doFilterInternal(HttpServletRequest request,
									HttpServletResponse response,
									FilterChain filterChain) throws ServletException, IOException {
		String token = extractToken(request);

		if (StringUtils.hasText(token) && jwtUtils.validateToken(token)) {
			String username = jwtUtils.getUsername(token);

			if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
				UserDetails userDetails = userDetailsService.loadUserByUsername(username);

				UsernamePasswordAuthenticationToken authentication =
						new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
				SecurityContextHolder.getContext().setAuthentication(authentication);

				log.debug("JWT 认证成功: user={}", username);
			}
		}

		filterChain.doFilter(request, response);
	}

	private String extractToken(HttpServletRequest request) {
		String header = request.getHeader(AUTH_HEADER);
		if (StringUtils.hasText(header) && header.startsWith(BEARER_PREFIX)) {
			return header.substring(BEARER_PREFIX.length());
		}
		return null;
	}
}
