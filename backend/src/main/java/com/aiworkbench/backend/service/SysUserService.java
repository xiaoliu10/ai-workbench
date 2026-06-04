package com.aiworkbench.backend.service;

import com.aiworkbench.backend.common.BizException;
import com.aiworkbench.backend.dto.UserDtos;
import com.aiworkbench.backend.entity.SysMenu;
import com.aiworkbench.backend.entity.SysRole;
import com.aiworkbench.backend.entity.SysUser;
import com.aiworkbench.backend.repository.SysMenuRepository;
import com.aiworkbench.backend.repository.SysRoleRepository;
import com.aiworkbench.backend.repository.SysUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SysUserService {

	private final SysUserRepository sysUserRepository;
	private final SysRoleRepository sysRoleRepository;
	private final SysMenuRepository sysMenuRepository;
	private final PasswordEncoder passwordEncoder;

	/**
	 * 分页查询用户
	 */
	@Transactional(readOnly = true)
	public Page<UserDtos.UserResponse> listUsers(String keyword, Integer status, Pageable pageable) {
		return sysUserRepository.searchUsers(keyword, status, pageable)
				.map(this::toUserResponse);
	}

	/**
	 * 获取用户详情
	 */
	@Transactional(readOnly = true)
	public UserDtos.UserResponse getUser(Long id) {
		SysUser user = sysUserRepository.findWithRolesById(id)
				.orElseThrow(() -> BizException.notFound("用户不存在"));
		return toUserResponse(user);
	}

	/**
	 * 创建用户
	 */
	@Transactional
	public UserDtos.UserResponse createUser(UserDtos.CreateUserRequest request) {
		if (sysUserRepository.existsByUsername(request.username())) {
			throw BizException.badRequest("用户名已存在: " + request.username());
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
		return toUserResponse(user);
	}

	/**
	 * 更新用户信息
	 */
	@Transactional
	public UserDtos.UserResponse updateUser(Long id, UserDtos.UpdateUserRequest request) {
		SysUser user = sysUserRepository.findById(id)
				.orElseThrow(() -> BizException.notFound("用户不存在"));

		if (request.nickname() != null) {
			user.setNickname(request.nickname());
		}
		if (request.email() != null) {
			user.setEmail(request.email());
		}
		if (request.phone() != null) {
			user.setPhone(request.phone());
		}
		if (request.avatar() != null) {
			user.setAvatar(request.avatar());
		}
		if (request.org() != null) {
			user.setOrg(request.org());
		}
		if (request.status() != null) {
			user.setStatus(request.status());
		}

		user = sysUserRepository.save(user);
		return toUserResponse(user);
	}

	/**
	 * 删除用户
	 */
	@Transactional
	public void deleteUser(Long id) {
		if (!sysUserRepository.existsById(id)) {
			throw BizException.notFound("用户不存在");
		}
		sysUserRepository.deleteById(id);
	}

	/**
	 * 修改密码
	 */
	@Transactional
	public void changePassword(Long id, UserDtos.ChangePasswordRequest request) {
		SysUser user = sysUserRepository.findById(id)
				.orElseThrow(() -> BizException.notFound("用户不存在"));

		if (!passwordEncoder.matches(request.oldPassword(), user.getPassword())) {
			throw BizException.badRequest("旧密码不正确");
		}

		user.setPassword(passwordEncoder.encode(request.newPassword()));
		sysUserRepository.save(user);
	}

	/**
	 * 分配角色
	 */
	@Transactional
	public UserDtos.UserResponse assignRoles(Long id, UserDtos.UpdateUserRolesRequest request) {
		SysUser user = sysUserRepository.findById(id)
				.orElseThrow(() -> BizException.notFound("用户不存在"));

		Set<SysRole> roles = new HashSet<>();
		if (request.roleIds() != null) {
			roles = request.roleIds().stream()
					.map(roleId -> sysRoleRepository.findById(roleId)
							.orElseThrow(() -> BizException.notFound("角色不存在: " + roleId)))
					.collect(Collectors.toSet());
		}

		user.setRoles(roles);
		user = sysUserRepository.save(user);
		return toUserResponse(user);
	}

	private UserDtos.UserResponse toUserResponse(SysUser user) {
		Set<UserDtos.RoleBrief> roleBriefs = user.getRoles().stream()
				.map(role -> new UserDtos.RoleBrief(role.getId(), role.getRoleCode(), role.getRoleName()))
				.collect(Collectors.toSet());

		return new UserDtos.UserResponse(
				user.getId(),
				user.getUsername(),
				user.getNickname(),
				user.getEmail(),
				user.getPhone(),
				user.getAvatar(),
				user.getOrg(),
				user.getStatus(),
				roleBriefs,
				user.getCreatedAt(),
				user.getUpdatedAt()
		);
	}
}
