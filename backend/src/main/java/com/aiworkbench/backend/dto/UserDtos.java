package com.aiworkbench.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Set;

/**
 * 用户管理 DTO
 */
public final class UserDtos {

	private UserDtos() {}

	public record UserResponse(
			Long id,
			String username,
			String nickname,
			String email,
			String phone,
			String avatar,
			String org,
			Integer status,
			Set<RoleBrief> roles,
			OffsetDateTime createdAt,
			OffsetDateTime updatedAt
	) {}

	public record RoleBrief(
			Long id,
			String roleCode,
			String roleName
	) {}

	public record CreateUserRequest(
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

	public record UpdateUserRequest(
			String nickname,
			String email,
			String phone,
			String avatar,
			String org,
			Integer status
	) {}

	public record UpdateUserRolesRequest(
			@NotNull(message = "角色列表不能为空")
			Set<Long> roleIds
	) {}

	public record ChangePasswordRequest(
			@NotBlank(message = "旧密码不能为空")
			String oldPassword,
			@NotBlank(message = "新密码不能为空")
			@Size(min = 6, max = 100, message = "新密码长度 6-100")
			String newPassword
	) {}
}
