package com.aiworkbench.backend.controller;

import com.aiworkbench.backend.common.R;
import com.aiworkbench.backend.dto.AuthDtos;
import com.aiworkbench.backend.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

	private final AuthService authService;

	/**
	 * 用户登录
	 */
	@PostMapping("/login")
	public R<AuthDtos.LoginResponse> login(@Valid @RequestBody AuthDtos.LoginRequest request) {
		return R.ok(authService.login(request));
	}

	/**
	 * 用户注册
	 */
	@PostMapping("/register")
	public R<AuthDtos.LoginResponse> register(@Valid @RequestBody AuthDtos.RegisterRequest request) {
		return R.ok(authService.register(request));
	}
}
