package com.aiworkbench.backend.controller;

import com.aiworkbench.backend.common.R;
import com.aiworkbench.backend.dto.UserDtos;
import com.aiworkbench.backend.service.SysUserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/system/users")
@RequiredArgsConstructor
public class SysUserController {

	private final SysUserService sysUserService;

	/**
	 * 分页查询用户列表
	 */
	@GetMapping
	@PreAuthorize("hasAuthority('sys:user:list')")
	public R<Page<UserDtos.UserResponse>> listUsers(
			@RequestParam(required = false) String keyword,
			@RequestParam(required = false) Integer status,
			@PageableDefault(size = 20) Pageable pageable) {
		return R.ok(sysUserService.listUsers(keyword, status, pageable));
	}

	/**
	 * 获取用户详情
	 */
	@GetMapping("/{id}")
	@PreAuthorize("hasAuthority('sys:user:list')")
	public R<UserDtos.UserResponse> getUser(@PathVariable Long id) {
		return R.ok(sysUserService.getUser(id));
	}

	/**
	 * 创建用户
	 */
	@PostMapping
	@PreAuthorize("hasAuthority('sys:user:create')")
	public R<UserDtos.UserResponse> createUser(@Valid @RequestBody UserDtos.CreateUserRequest request) {
		return R.ok(sysUserService.createUser(request));
	}

	/**
	 * 更新用户信息
	 */
	@PutMapping("/{id}")
	@PreAuthorize("hasAuthority('sys:user:update')")
	public R<UserDtos.UserResponse> updateUser(
			@PathVariable Long id,
			@Valid @RequestBody UserDtos.UpdateUserRequest request) {
		return R.ok(sysUserService.updateUser(id, request));
	}

	/**
	 * 删除用户
	 */
	@DeleteMapping("/{id}")
	@PreAuthorize("hasAuthority('sys:user:delete')")
	public R<Void> deleteUser(@PathVariable Long id) {
		sysUserService.deleteUser(id);
		return R.ok();
	}

	/**
	 * 修改密码
	 */
	@PutMapping("/{id}/password")
	@PreAuthorize("hasAuthority('sys:user:update')")
	public R<Void> changePassword(
			@PathVariable Long id,
			@Valid @RequestBody UserDtos.ChangePasswordRequest request) {
		sysUserService.changePassword(id, request);
		return R.ok();
	}

	/**
	 * 分配角色
	 */
	@PutMapping("/{id}/roles")
	@PreAuthorize("hasAuthority('sys:user:assign-role')")
	public R<UserDtos.UserResponse> assignRoles(
			@PathVariable Long id,
			@Valid @RequestBody UserDtos.UpdateUserRolesRequest request) {
		return R.ok(sysUserService.assignRoles(id, request));
	}
}
