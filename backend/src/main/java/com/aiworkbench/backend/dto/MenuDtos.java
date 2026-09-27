package com.aiworkbench.backend.dto;

import jakarta.validation.constraints.NotBlank;

import java.time.OffsetDateTime;
import java.util.List;

/**
 * 菜单管理 DTO
 */
public final class MenuDtos {

	private MenuDtos() {}

	public record MenuTreeResponse(
			Long id,
			Long parentId,
			String menuName,
			String menuCode,
			String path,
			String component,
			String icon,
			Integer sortOrder,
			String menuType,
			String permission,
			Integer visible,
			Integer status,
			List<MenuTreeResponse> children,
			OffsetDateTime createdAt,
			OffsetDateTime updatedAt
	) {}

	public record CreateMenuRequest(
			Long parentId,
			@NotBlank(message = "菜单名称不能为空")
			String menuName,
			@NotBlank(message = "菜单编码不能为空")
			String menuCode,
			String path,
			String component,
			String icon,
			Integer sortOrder,
			String menuType,
			String permission,
			Integer visible
	) {}

	public record UpdateMenuRequest(
			Long parentId,
			String menuName,
			String path,
			String component,
			String icon,
			Integer sortOrder,
			String menuType,
			String permission,
			Integer visible,
			Integer status
	) {}
}
