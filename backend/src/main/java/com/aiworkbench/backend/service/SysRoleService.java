package com.aiworkbench.backend.service;

import com.aiworkbench.backend.common.BizException;
import com.aiworkbench.backend.dto.RoleDtos;
import com.aiworkbench.backend.entity.SysMenu;
import com.aiworkbench.backend.entity.SysRole;
import com.aiworkbench.backend.repository.SysMenuRepository;
import com.aiworkbench.backend.repository.SysRoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SysRoleService {

	private final SysRoleRepository sysRoleRepository;
	private final SysMenuRepository sysMenuRepository;

	/**
	 * 分页查询角色
	 */
	@Transactional(readOnly = true)
	public Page<RoleDtos.RoleResponse> listRoles(String keyword, Integer status, Pageable pageable) {
		return sysRoleRepository.searchRoles(keyword, status, pageable)
				.map(this::toRoleResponse);
	}

	/**
	 * 查询所有角色（简要信息，用于下拉选择）
	 */
	@Transactional(readOnly = true)
	public List<RoleDtos.RoleResponse> listAllRoles() {
		return sysRoleRepository.findAll().stream()
				.map(this::toRoleResponse)
				.toList();
	}

	/**
	 * 获取角色详情
	 */
	@Transactional(readOnly = true)
	public RoleDtos.RoleResponse getRole(Long id) {
		SysRole role = sysRoleRepository.findById(id)
				.orElseThrow(() -> BizException.notFound("角色不存在"));
		return toRoleResponse(role);
	}

	/**
	 * 创建角色
	 */
	@Transactional
	public RoleDtos.RoleResponse createRole(RoleDtos.CreateRoleRequest request) {
		if (sysRoleRepository.existsByRoleCode(request.roleCode())) {
			throw BizException.badRequest("角色编码已存在: " + request.roleCode());
		}

		SysRole role = SysRole.builder()
				.roleCode(request.roleCode())
				.roleName(request.roleName())
				.description(request.description())
				.sortOrder(request.sortOrder() != null ? request.sortOrder() : 0)
				.status(1)
				.build();

		role = sysRoleRepository.save(role);
		return toRoleResponse(role);
	}

	/**
	 * 更新角色
	 */
	@Transactional
	public RoleDtos.RoleResponse updateRole(Long id, RoleDtos.UpdateRoleRequest request) {
		SysRole role = sysRoleRepository.findById(id)
				.orElseThrow(() -> BizException.notFound("角色不存在"));

		if (request.roleName() != null) {
			role.setRoleName(request.roleName());
		}
		if (request.description() != null) {
			role.setDescription(request.description());
		}
		if (request.sortOrder() != null) {
			role.setSortOrder(request.sortOrder());
		}
		if (request.status() != null) {
			role.setStatus(request.status());
		}

		role = sysRoleRepository.save(role);
		return toRoleResponse(role);
	}

	/**
	 * 删除角色
	 */
	@Transactional
	public void deleteRole(Long id) {
		if (!sysRoleRepository.existsById(id)) {
			throw BizException.notFound("角色不存在");
		}
		sysRoleRepository.deleteById(id);
	}

	/**
	 * 分配菜单权限
	 */
	@Transactional
	public RoleDtos.RoleResponse assignMenus(Long id, RoleDtos.UpdateRoleMenusRequest request) {
		SysRole role = sysRoleRepository.findById(id)
				.orElseThrow(() -> BizException.notFound("角色不存在"));

		Set<SysMenu> menus = new HashSet<>();
		if (request.menuIds() != null) {
			menus = request.menuIds().stream()
					.map(menuId -> sysMenuRepository.findById(menuId)
							.orElseThrow(() -> BizException.notFound("菜单不存在: " + menuId)))
					.collect(Collectors.toSet());
		}

		role.setMenus(menus);
		role = sysRoleRepository.save(role);
		return toRoleResponse(role);
	}

	private RoleDtos.RoleResponse toRoleResponse(SysRole role) {
		List<RoleDtos.MenuBrief> menuBriefs = role.getMenus().stream()
				.map(menu -> new RoleDtos.MenuBrief(
						menu.getId(), menu.getMenuName(), menu.getMenuCode(),
						menu.getPath(), menu.getIcon()))
				.toList();

		return new RoleDtos.RoleResponse(
				role.getId(),
				role.getRoleCode(),
				role.getRoleName(),
				role.getDescription(),
				role.getSortOrder(),
				role.getStatus(),
				menuBriefs,
				role.getCreatedAt(),
				role.getUpdatedAt()
		);
	}
}
