package com.aiworkbench.backend.service;

import com.aiworkbench.backend.common.BizException;
import com.aiworkbench.backend.dto.MenuDtos;
import com.aiworkbench.backend.entity.SysMenu;
import com.aiworkbench.backend.entity.SysMenu.MenuType;
import com.aiworkbench.backend.repository.SysMenuRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class SysMenuService {

	private final SysMenuRepository sysMenuRepository;

	/**
	 * 获取菜单树（管理端 - 全量菜单）
	 */
	@Transactional(readOnly = true)
	public List<MenuDtos.MenuTreeResponse> getMenuTree() {
		List<SysMenu> allMenus = sysMenuRepository.findAllActive();
		return buildTree(null, allMenus);
	}

	/**
	 * 获取指定角色关联的菜单树（用户登录后获取有权限的菜单）
	 */
	@Transactional(readOnly = true)
	public List<MenuDtos.MenuTreeResponse> getMenuTreeByRoleIds(List<Long> roleIds) {
		if (roleIds == null || roleIds.isEmpty()) {
			return List.of();
		}
		List<SysMenu> menus = sysMenuRepository.findByRoleIds(roleIds);
		return buildTree(null, menus);
	}

	/**
	 * 获取菜单详情
	 */
	@Transactional(readOnly = true)
	public MenuDtos.MenuTreeResponse getMenu(Long id) {
		SysMenu menu = sysMenuRepository.findById(id)
				.orElseThrow(() -> BizException.notFound("菜单不存在"));
		return toMenuResponse(menu);
	}

	/**
	 * 创建菜单
	 */
	@Transactional
	public MenuDtos.MenuTreeResponse createMenu(MenuDtos.CreateMenuRequest request) {
		if (sysMenuRepository.existsByMenuCode(request.menuCode())) {
			throw BizException.badRequest("菜单编码已存在: " + request.menuCode());
		}

		SysMenu parent = null;
		if (request.parentId() != null && request.parentId() > 0) {
			parent = sysMenuRepository.findById(request.parentId())
					.orElseThrow(() -> BizException.notFound("父菜单不存在"));
		}

		SysMenu menu = SysMenu.builder()
				.parent(parent)
				.menuName(request.menuName())
				.menuCode(request.menuCode())
				.path(request.path())
				.component(request.component())
				.icon(request.icon())
				.sortOrder(request.sortOrder() != null ? request.sortOrder() : 0)
				.menuType(parseMenuType(request.menuType()))
				.permission(request.permission())
				.visible(request.visible() != null ? request.visible() : 1)
				.status(1)
				.build();

		menu = sysMenuRepository.save(menu);
		return toMenuResponse(menu);
	}

	/**
	 * 更新菜单
	 */
	@Transactional
	public MenuDtos.MenuTreeResponse updateMenu(Long id, MenuDtos.UpdateMenuRequest request) {
		SysMenu menu = sysMenuRepository.findById(id)
				.orElseThrow(() -> BizException.notFound("菜单不存在"));

		if (request.parentId() != null) {
			if (request.parentId() > 0) {
				SysMenu parent = sysMenuRepository.findById(request.parentId())
						.orElseThrow(() -> BizException.notFound("父菜单不存在"));
				menu.setParent(parent);
			} else {
				menu.setParent(null);
			}
		}
		if (request.menuName() != null) {
			menu.setMenuName(request.menuName());
		}
		if (request.path() != null) {
			menu.setPath(request.path());
		}
		if (request.component() != null) {
			menu.setComponent(request.component());
		}
		if (request.icon() != null) {
			menu.setIcon(request.icon());
		}
		if (request.sortOrder() != null) {
			menu.setSortOrder(request.sortOrder());
		}
		if (request.menuType() != null) {
			menu.setMenuType(parseMenuType(request.menuType()));
		}
		if (request.permission() != null) {
			menu.setPermission(request.permission());
		}
		if (request.visible() != null) {
			menu.setVisible(request.visible());
		}
		if (request.status() != null) {
			menu.setStatus(request.status());
		}

		menu = sysMenuRepository.save(menu);
		return toMenuResponse(menu);
	}

	/**
	 * 删除菜单
	 */
	@Transactional
	public void deleteMenu(Long id) {
		if (!sysMenuRepository.existsById(id)) {
			throw BizException.notFound("菜单不存在");
		}
		sysMenuRepository.deleteById(id);
	}

	// ==================== 私有方法 ====================

	/**
	 * 递归构建菜单树
	 */
	private List<MenuDtos.MenuTreeResponse> buildTree(Long parentId, List<SysMenu> allMenus) {
		List<MenuDtos.MenuTreeResponse> result = new ArrayList<>();
		for (SysMenu menu : allMenus) {
			Long menuParentId = menu.getParent() != null ? menu.getParent().getId() : null;
			if (Objects.equals(parentId, menuParentId)) {
				MenuDtos.MenuTreeResponse dto = new MenuDtos.MenuTreeResponse(
						menu.getId(),
						menuParentId,
						menu.getMenuName(),
						menu.getMenuCode(),
						menu.getPath(),
						menu.getComponent(),
						menu.getIcon(),
						menu.getSortOrder(),
						menu.getMenuType().name(),
						menu.getPermission(),
						menu.getVisible(),
						menu.getStatus(),
						buildTree(menu.getId(), allMenus),
						menu.getCreatedAt(),
						menu.getUpdatedAt()
				);
				result.add(dto);
			}
		}
		return result;
	}

	private MenuType parseMenuType(String menuType) {
		if (menuType == null) {
			return MenuType.MENU;
		}
		try {
			return MenuType.valueOf(menuType.toUpperCase());
		} catch (IllegalArgumentException e) {
			return MenuType.MENU;
		}
	}

	private MenuDtos.MenuTreeResponse toMenuResponse(SysMenu menu) {
		return new MenuDtos.MenuTreeResponse(
				menu.getId(),
				menu.getParent() != null ? menu.getParent().getId() : null,
				menu.getMenuName(),
				menu.getMenuCode(),
				menu.getPath(),
				menu.getComponent(),
				menu.getIcon(),
				menu.getSortOrder(),
				menu.getMenuType().name(),
				menu.getPermission(),
				menu.getVisible(),
				menu.getStatus(),
				List.of(),
				menu.getCreatedAt(),
				menu.getUpdatedAt()
		);
	}
}
