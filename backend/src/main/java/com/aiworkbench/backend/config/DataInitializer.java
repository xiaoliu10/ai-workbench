package com.aiworkbench.backend.config;

import com.aiworkbench.backend.entity.SysMenu;
import com.aiworkbench.backend.entity.SysMenu.MenuType;
import com.aiworkbench.backend.entity.SysRole;
import com.aiworkbench.backend.entity.SysUser;
import com.aiworkbench.backend.repository.SysMenuRepository;
import com.aiworkbench.backend.repository.SysRoleRepository;
import com.aiworkbench.backend.repository.SysUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

/**
 * 系统初始数据：角色、菜单、管理员用户
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

	private final SysRoleRepository roleRepository;
	private final SysMenuRepository menuRepository;
	private final SysUserRepository userRepository;
	private final PasswordEncoder passwordEncoder;

	@Override
	@Transactional
	public void run(String... args) {
		log.info("开始初始化系统数据...");

		initRoles();
		initMenus();
		initAdminUser();
		assignRoleMenus();

		log.info("系统数据初始化完成");
	}

	private void initRoles() {
		createRoleIfAbsent("admin", "系统管理员", "拥有系统所有权限", 1);
		createRoleIfAbsent("operator", "模型运营", "模型服务与AI工作台管理", 2);
		createRoleIfAbsent("auditor", "审计员", "审计日志与合规查看", 3);
		createRoleIfAbsent("user", "普通用户", "基础使用权限", 4);
	}

	private void createRoleIfAbsent(String code, String name, String desc, int sort) {
		if (!roleRepository.existsByRoleCode(code)) {
			roleRepository.save(SysRole.builder()
					.roleCode(code)
					.roleName(name)
					.description(desc)
					.sortOrder(sort)
					.status(1)
					.build());
			log.info("创建角色: {} ({})", name, code);
		}
	}

	private void initMenus() {
		// ===== 一级目录 =====
		SysMenu systemDir = createMenuIfAbsent(null, "系统管理", "system", "/system", null, "Settings", 1, MenuType.DIRECTORY, null);
		SysMenu workspaceDir = createMenuIfAbsent(null, "AI 工作台", "workspace", "/workspace", null, "Brain", 2, MenuType.DIRECTORY, null);
		SysMenu monitorDir = createMenuIfAbsent(null, "运维监控", "monitor", "/monitor", null, "Activity", 3, MenuType.DIRECTORY, null);

		// ===== 系统管理 - 二级菜单 =====
		SysMenu modelProvider = createMenuIfAbsent(systemDir, "模型服务商", "model-provider", "/system/model-provider", "system/ModelProvider", "Server", 1, MenuType.MENU, "sys:model-provider:list");
		SysMenu userManage = createMenuIfAbsent(systemDir, "用户管理", "user-manage", "/system/users", "system/UserManage", "Users", 2, MenuType.MENU, "sys:user:list");
		SysMenu roleManage = createMenuIfAbsent(systemDir, "角色管理", "role-manage", "/system/roles", "system/RoleManage", "Shield", 3, MenuType.MENU, "sys:role:list");
		SysMenu menuManage = createMenuIfAbsent(systemDir, "菜单管理", "menu-manage", "/system/menus", "system/MenuManage", "Menu", 4, MenuType.MENU, "sys:menu:list");

		// 用户管理 - 按钮
		createMenuIfAbsent(userManage, "创建用户", "user-create", null, null, null, 1, MenuType.BUTTON, "sys:user:create");
		createMenuIfAbsent(userManage, "编辑用户", "user-update", null, null, null, 2, MenuType.BUTTON, "sys:user:update");
		createMenuIfAbsent(userManage, "删除用户", "user-delete", null, null, null, 3, MenuType.BUTTON, "sys:user:delete");
		createMenuIfAbsent(userManage, "分配角色", "user-assign-role", null, null, null, 4, MenuType.BUTTON, "sys:user:assign-role");

		// 角色管理 - 按钮
		createMenuIfAbsent(roleManage, "创建角色", "role-create", null, null, null, 1, MenuType.BUTTON, "sys:role:create");
		createMenuIfAbsent(roleManage, "编辑角色", "role-update", null, null, null, 2, MenuType.BUTTON, "sys:role:update");
		createMenuIfAbsent(roleManage, "删除角色", "role-delete", null, null, null, 3, MenuType.BUTTON, "sys:role:delete");
		createMenuIfAbsent(roleManage, "分配菜单", "role-assign-menu", null, null, null, 4, MenuType.BUTTON, "sys:role:assign-menu");

		// 菜单管理 - 按钮
		createMenuIfAbsent(menuManage, "创建菜单", "menu-create", null, null, null, 1, MenuType.BUTTON, "sys:menu:create");
		createMenuIfAbsent(menuManage, "编辑菜单", "menu-update", null, null, null, 2, MenuType.BUTTON, "sys:menu:update");
		createMenuIfAbsent(menuManage, "删除菜单", "menu-delete", null, null, null, 3, MenuType.BUTTON, "sys:menu:delete");

		// ===== AI 工作台 - 二级菜单 =====
		createMenuIfAbsent(workspaceDir, "对话工作区", "chat", "/workspace/chat", "workspace/Chat", "MessageSquare", 1, MenuType.MENU, "workspace:chat");
		createMenuIfAbsent(workspaceDir, "提示词模板", "prompt-template", "/workspace/prompts", "workspace/Prompts", "FileText", 2, MenuType.MENU, "workspace:prompt");
		createMenuIfAbsent(workspaceDir, "知识库", "knowledge-base", "/workspace/knowledge", "workspace/Knowledge", "BookOpen", 3, MenuType.MENU, "workspace:knowledge");
		createMenuIfAbsent(workspaceDir, "调用审计", "api-audit", "/workspace/audit", "workspace/Audit", "ClipboardList", 4, MenuType.MENU, "workspace:audit");

		// ===== 运维监控 - 二级菜单 =====
		createMenuIfAbsent(monitorDir, "模型健康检查", "model-health", "/monitor/health", "monitor/Health", "HeartPulse", 1, MenuType.MENU, "monitor:health");
		createMenuIfAbsent(monitorDir, "配额看板", "quota-dashboard", "/monitor/quota", "monitor/Quota", "Gauge", 2, MenuType.MENU, "monitor:quota");
		createMenuIfAbsent(monitorDir, "异常告警", "alert", "/monitor/alerts", "monitor/Alerts", "AlertTriangle", 3, MenuType.MENU, "monitor:alert");
	}

	private SysMenu createMenuIfAbsent(SysMenu parent, String name, String code, String path,
									   String component, String icon, int sort,
									   MenuType type, String permission) {
		if (!menuRepository.existsByMenuCode(code)) {
			SysMenu menu = SysMenu.builder()
					.parent(parent)
					.menuName(name)
					.menuCode(code)
					.path(path)
					.component(component)
					.icon(icon)
					.sortOrder(sort)
					.menuType(type)
					.permission(permission)
					.visible(1)
					.status(1)
					.build();
			menuRepository.save(menu);
			log.info("创建菜单: {} ({})", name, code);
			return menu;
		}
		// 已存在则查询返回
		return menuRepository.findByMenuCode(code).orElse(null);
	}

	private void initAdminUser() {
		if (!userRepository.existsByUsername("admin")) {
			SysUser admin = SysUser.builder()
					.username("admin")
					.password(passwordEncoder.encode("admin123"))
					.nickname("系统管理员")
					.email("admin@aiworkbench.com")
					.org("平台部")
					.status(1)
					.build();

			SysRole adminRole = roleRepository.findByRoleCode("admin").orElseThrow();
			admin.setRoles(Set.of(adminRole));

			userRepository.save(admin);
			log.info("创建管理员用户: admin");
		}
	}

	private void assignRoleMenus() {
		List<SysMenu> allMenus = menuRepository.findAll();

		// admin: 所有菜单
		assignMenusToRole("admin", allMenus);

		// operator: 系统管理(模型服务商) + AI工作台 + 运维监控
		List<String> operatorCodes = List.of(
				"system", "model-provider",
				"workspace", "chat", "prompt-template", "knowledge-base", "api-audit",
				"monitor", "model-health", "quota-dashboard", "alert"
		);
		assignMenusToRole("operator", allMenus.stream()
				.filter(m -> operatorCodes.contains(m.getMenuCode()))
				.toList());

		// auditor: 系统管理(部分) + 调用审计 + 运维监控
		List<String> auditorCodes = List.of(
				"system", "model-provider", "user-manage", "user-manage", "role-manage", "menu-manage",
				"workspace", "api-audit",
				"monitor", "model-health", "quota-dashboard", "alert"
		);
		assignMenusToRole("auditor", allMenus.stream()
				.filter(m -> auditorCodes.contains(m.getMenuCode()))
				.toList());

		// user: AI工作台(对话、提示词、知识库)
		List<String> userCodes = List.of("workspace", "chat", "prompt-template", "knowledge-base");
		assignMenusToRole("user", allMenus.stream()
				.filter(m -> userCodes.contains(m.getMenuCode()))
				.toList());
	}

	private void assignMenusToRole(String roleCode, List<SysMenu> menus) {
		roleRepository.findByRoleCode(roleCode).ifPresent(role -> {
			if (role.getMenus().isEmpty()) {
				role.setMenus(Set.copyOf(menus));
				roleRepository.save(role);
				log.info("分配 {} 个菜单给角色: {}", menus.size(), roleCode);
			}
		});
	}
}
