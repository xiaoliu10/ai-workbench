package com.aiworkbench.backend.controller;

import com.aiworkbench.backend.common.R;
import com.aiworkbench.backend.dto.MenuDtos;
import com.aiworkbench.backend.entity.SysMenu;
import com.aiworkbench.backend.entity.SysRole;
import com.aiworkbench.backend.entity.SysUser;
import com.aiworkbench.backend.repository.SysUserRepository;
import com.aiworkbench.backend.service.SysMenuService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/system/menus")
@RequiredArgsConstructor
public class SysMenuController {

	private final SysMenuService sysMenuService;
	private final SysUserRepository sysUserRepository;

	/**
	 * 获取菜单树（管理端 - 全量菜单）
	 */
	@GetMapping("/tree")
	@PreAuthorize("hasAuthority('sys:menu:list')")
	public R<List<MenuDtos.MenuTreeResponse>> getMenuTree() {
		return R.ok(sysMenuService.getMenuTree());
	}

	/**
	 * 获取当前用户的菜单树（登录后获取有权限的菜单）
	 */
	@GetMapping("/current")
	public R<List<MenuDtos.MenuTreeResponse>> getCurrentUserMenus(
			@AuthenticationPrincipal UserDetails userDetails) {
		SysUser user = sysUserRepository.findByUsername(userDetails.getUsername())
				.orElseThrow(() -> new IllegalStateException("当前用户不存在"));

		List<Long> roleIds = user.getRoles().stream()
				.map(SysRole::getId)
				.toList();

		return R.ok(sysMenuService.getMenuTreeByRoleIds(roleIds));
	}

	/**
	 * 获取菜单详情
	 */
	@GetMapping("/{id}")
	@PreAuthorize("hasAuthority('sys:menu:list')")
	public R<MenuDtos.MenuTreeResponse> getMenu(@PathVariable Long id) {
		return R.ok(sysMenuService.getMenu(id));
	}

	/**
	 * 创建菜单
	 */
	@PostMapping
	@PreAuthorize("hasAuthority('sys:menu:create')")
	public R<MenuDtos.MenuTreeResponse> createMenu(@Valid @RequestBody MenuDtos.CreateMenuRequest request) {
		return R.ok(sysMenuService.createMenu(request));
	}

	/**
	 * 更新菜单
	 */
	@PutMapping("/{id}")
	@PreAuthorize("hasAuthority('sys:menu:update')")
	public R<MenuDtos.MenuTreeResponse> updateMenu(
			@PathVariable Long id,
			@Valid @RequestBody MenuDtos.UpdateMenuRequest request) {
		return R.ok(sysMenuService.updateMenu(id, request));
	}

	/**
	 * 删除菜单
	 */
	@DeleteMapping("/{id}")
	@PreAuthorize("hasAuthority('sys:menu:delete')")
	public R<Void> deleteMenu(@PathVariable Long id) {
		sysMenuService.deleteMenu(id);
		return R.ok();
	}
}
