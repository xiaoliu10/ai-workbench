package com.aiworkbench.backend.service;

import com.aiworkbench.backend.common.BizException;
import com.aiworkbench.backend.dto.AuthDtos;
import com.aiworkbench.backend.entity.SysRole;
import com.aiworkbench.backend.entity.SysUser;
import com.aiworkbench.backend.repository.SysRoleRepository;
import com.aiworkbench.backend.repository.SysUserRepository;
import com.aiworkbench.backend.security.CustomUserDetailsService;
import com.aiworkbench.backend.security.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthService {

	private static final String DEFAULT_ROLE_CODE = "user";

	private final SysUserRepository sysUserRepository;
	private final SysRoleRepository sysRoleRepository;
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
				.roles(new HashSet<>(Set.of(resolveDefaultRole())))
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

	/**
	 * 注册用户的默认角色，缺失时明确报错而不是静默零权限
	 */
	private SysRole resolveDefaultRole() {
		return sysRoleRepository.findByRoleCode(DEFAULT_ROLE_CODE)
				.orElseThrow(() -> new BizException("默认角色未初始化: " + DEFAULT_ROLE_CODE));
	}
}
