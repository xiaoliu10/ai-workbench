package com.aiworkbench.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * 认证相关 DTO
 */
public final class AuthDtos {

	private AuthDtos() {}

	public record LoginRequest(
			@NotBlank(message = "用户名不能为空")
			String username,
			@NotBlank(message = "密码不能为空")
			String password
	) {}

	public record LoginResponse(
			String token,
			String username,
			String nickname,
			Long userId
	) {}

	public record RegisterRequest(
			@NotBlank(message = "用户名不能为空")
			@Size(min = 3, max = 50, message = "用户名长度 3-50")
			String username,
			@NotBlank(message = "密码不能为空")
			@Size(min = 6, max = 100, message = "密码长度 6-100")
			String password,
			String nickname,
			String email,
			String phone,
			String org
	) {}
}
