package com.aiworkbench.backend.controller;

import com.aiworkbench.backend.common.R;
import com.aiworkbench.backend.dto.RoleDtos;
import com.aiworkbench.backend.service.SysRoleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/system/roles")
@RequiredArgsConstructor
public class SysRoleController {

	private final SysRoleService sysRoleService;

	/**
	 * 分页查询角色列表
	 */
	@GetMapping
	@PreAuthorize("hasAuthority('sys:role:list')")
	public R<Page<RoleDtos.RoleResponse>> listRoles(
			@RequestParam(required = false) String keyword,
			@RequestParam(required = false) Integer status,
			@PageableDefault(size = 20) Pageable pageable) {
		return R.ok(sysRoleService.listRoles(keyword, status, pageable));
	}

	/**
	 * 查询所有角色（简要信息，用于下拉选择）
	 */
	@GetMapping("/all")
	@PreAuthorize("hasAuthority('sys:role:list')")
	public R<List<RoleDtos.RoleResponse>> listAllRoles() {
		return R.ok(sysRoleService.listAllRoles());
	}

	/**
	 * 获取角色详情
	 */
	@GetMapping("/{id}")
	@PreAuthorize("hasAuthority('sys:role:list')")
	public R<RoleDtos.RoleResponse> getRole(@PathVariable Long id) {
		return R.ok(sysRoleService.getRole(id));
	}

	/**
	 * 创建角色
	 */
	@PostMapping
	@PreAuthorize("hasAuthority('sys:role:create')")
	public R<RoleDtos.RoleResponse> createRole(@Valid @RequestBody RoleDtos.CreateRoleRequest request) {
		return R.ok(sysRoleService.createRole(request));
	}

	/**
	 * 更新角色
	 */
	@PutMapping("/{id}")
	@PreAuthorize("hasAuthority('sys:role:update')")
	public R<RoleDtos.RoleResponse> updateRole(
			@PathVariable Long id,
			@Valid @RequestBody RoleDtos.UpdateRoleRequest request) {
		return R.ok(sysRoleService.updateRole(id, request));
	}

	/**
	 * 删除角色
	 */
	@DeleteMapping("/{id}")
	@PreAuthorize("hasAuthority('sys:role:delete')")
	public R<Void> deleteRole(@PathVariable Long id) {
		sysRoleService.deleteRole(id);
		return R.ok();
	}

	/**
	 * 分配菜单权限
	 */
	@PutMapping("/{id}/menus")
	@PreAuthorize("hasAuthority('sys:role:assign-menu')")
	public R<RoleDtos.RoleResponse> assignMenus(
			@PathVariable Long id,
			@Valid @RequestBody RoleDtos.UpdateRoleMenusRequest request) {
		return R.ok(sysRoleService.assignMenus(id, request));
	}
}
