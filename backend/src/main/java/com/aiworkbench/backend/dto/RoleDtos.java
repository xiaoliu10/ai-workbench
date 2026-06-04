package com.aiworkbench.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

/**
 * 角色管理 DTO
 */
public final class RoleDtos {

	private RoleDtos() {}

	public record RoleResponse(
			Long id,
			String roleCode,
			String roleName,
			String description,
			Integer sortOrder,
			Integer status,
			List<MenuBrief> menus,
			LocalDateTime createdAt,
			LocalDateTime updatedAt
	) {}

	public record MenuBrief(
			Long id,
			String menuName,
			String menuCode,
			String path,
			String icon
	) {}

	public record CreateRoleRequest(
			@NotBlank(message = "角色编码不能为空")
			String roleCode,
			@NotBlank(message = "角色名称不能为空")
			String roleName,
			String description,
			Integer sortOrder
	) {}

	public record UpdateRoleRequest(
			String roleName,
			String description,
			Integer sortOrder,
			Integer status
	) {}

	public record UpdateRoleMenusRequest(
			@NotNull(message = "菜单列表不能为空")
			Set<Long> menuIds
	) {}
}
