package com.aiworkbench.backend.service;

import com.aiworkbench.backend.dto.AuthDtos;
import com.aiworkbench.backend.entity.SysUser;
import com.aiworkbench.backend.repository.SysUserRepository;
import com.aiworkbench.backend.security.CustomUserDetailsService;
import com.aiworkbench.backend.security.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthService {

	private final SysUserRepository sysUserRepository;
	private final PasswordEncoder passwordEncoder;
	private final JwtUtils jwtUtils;
	private final CustomUserDetailsService userDetailsService;

	/**
	 * 用户登录
	 */
	@Transactional(readOnly = true)
	public AuthDtos.LoginResponse login(AuthDtos.LoginRequest request) {
		SysUser user = sysUserRepository.findByUsername(request.username())
				.orElseThrow(() -> new IllegalArgumentException("用户名或密码错误"));

		if (user.getStatus() != 1) {
			throw new IllegalArgumentException("用户已被禁用");
		}

		if (!passwordEncoder.matches(request.password(), user.getPassword())) {
			throw new IllegalArgumentException("用户名或密码错误");
		}

		List<String> roleCodes = userDetailsService.getUserRoleCodes(user);
		String token = jwtUtils.generateToken(user.getId(), user.getUsername(), roleCodes);

		return new AuthDtos.LoginResponse(
				token,
				user.getUsername(),
				user.getNickname(),
				user.getId()
		);
	}

	/**
	 * 用户注册
	 */
	@Transactional
	public AuthDtos.LoginResponse register(AuthDtos.RegisterRequest request) {
		if (sysUserRepository.existsByUsername(request.username())) {
			throw new IllegalArgumentException("用户名已存在");
		}

		SysUser user = SysUser.builder()
				.username(request.username())
				.password(passwordEncoder.encode(request.password()))
				.nickname(request.nickname())
				.email(request.email())
				.phone(request.phone())
				.org(request.org())
				.status(1)
				.build();

		user = sysUserRepository.save(user);

		List<String> roleCodes = userDetailsService.getUserRoleCodes(user);
		String token = jwtUtils.generateToken(user.getId(), user.getUsername(), roleCodes);

		return new AuthDtos.LoginResponse(
				token,
				user.getUsername(),
				user.getNickname(),
				user.getId()
		);
	}
}
