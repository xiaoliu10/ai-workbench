package com.aiworkbench.backend.admin;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

@Service
class AdminConfigurationService {

	private final AtomicLong providerIds = new AtomicLong(4);
	private final AtomicLong userIds = new AtomicLong(5);

	private final List<ModelProviderResponse> providers = new ArrayList<>();
	private final List<AdminUserResponse> users = new ArrayList<>();
	private final List<PermissionMatrixRowResponse> permissionMatrix = new ArrayList<>();
	private final List<MenuGroupResponse> menus = new ArrayList<>();
	private final Map<String, RoleMenuAssignmentResponse> menuAssignments = new LinkedHashMap<>();
	private RegistrationPolicyResponse registrationPolicy = new RegistrationPolicyResponse(
			"AI Workbench",
			"普通用户",
			"管理员审核",
			"@company.com"
	);

	AdminConfigurationService() {
		seedProviders();
		seedUsers();
		seedPermissions();
		seedMenus();
	}

	synchronized AdminOverviewResponse overview() {
		long pendingProviders = providers.stream().filter(provider -> "待验证".equals(provider.status())).count();
		long pendingUsers = users.stream().filter(user -> "待审核".equals(user.status())).count();

		return new AdminOverviewResponse(
				List.of(
						new AdminMetricResponse("服务商", String.valueOf(providers.size()), pendingProviders + " 个待验证"),
						new AdminMetricResponse("注册用户", String.valueOf(users.size()), pendingUsers + " 个待审核"),
						new AdminMetricResponse("角色策略", "4", "覆盖 " + permissionMatrix.size() + " 个模块"),
						new AdminMetricResponse("菜单入口", String.valueOf(totalMenuEntries()), menus.size() + " 个一级目录")
				),
				List.of(
						new SystemHealthResponse("认证服务", "已启用", "运行正常"),
						new SystemHealthResponse("权限缓存", "已启用", "运行正常"),
						new SystemHealthResponse("模型网关", "已启用", "P95 320ms"),
						new SystemHealthResponse("审计日志", "已启用", "运行正常")
				)
		);
	}

	synchronized List<ModelProviderResponse> listProviders() {
		return List.copyOf(providers);
	}

	synchronized ModelProviderResponse createProvider(CreateModelProviderRequest request) {
		ModelProviderResponse provider = new ModelProviderResponse(
				providerIds.getAndIncrement(),
				request.name(),
				request.type(),
				request.endpoint(),
				"待验证",
				List.copyOf(request.models()),
				request.owner(),
				0
		);
		providers.add(provider);
		return provider;
	}

	synchronized RegistrationPolicyResponse registrationPolicy() {
		return registrationPolicy;
	}

	synchronized RegistrationPolicyResponse updateRegistrationPolicy(UpdateRegistrationPolicyRequest request) {
		registrationPolicy = new RegistrationPolicyResponse(
				request.defaultOrg(),
				request.defaultRole(),
				request.approvalMode(),
				request.emailDomainAllowlist()
		);
		return registrationPolicy;
	}

	synchronized List<AdminUserResponse> listUsers() {
		return List.copyOf(users);
	}

	synchronized AdminUserResponse registerUser(RegisterAdminUserRequest request) {
		AdminUserResponse user = new AdminUserResponse(
				userIds.getAndIncrement(),
				request.name(),
				request.account(),
				request.role(),
				request.org(),
				"待审核"
		);
		users.add(user);
		return user;
	}

	synchronized List<PermissionMatrixRowResponse> permissionMatrix() {
		return List.copyOf(permissionMatrix);
	}

	synchronized PermissionMatrixRowResponse updateRolePermission(String module, UpdateRolePermissionRequest request) {
		for (int i = 0; i < permissionMatrix.size(); i++) {
			PermissionMatrixRowResponse row = permissionMatrix.get(i);
			if (row.module().equals(module)) {
				PermissionMatrixRowResponse updated = new PermissionMatrixRowResponse(module, orderedPermissions(request.permissions()));
				permissionMatrix.set(i, updated);
				return updated;
			}
		}

		PermissionMatrixRowResponse created = new PermissionMatrixRowResponse(module, orderedPermissions(request.permissions()));
		permissionMatrix.add(created);
		return created;
	}

	synchronized List<MenuGroupResponse> menus() {
		return List.copyOf(menus);
	}

	synchronized List<RoleMenuAssignmentResponse> menuAssignments() {
		return List.copyOf(menuAssignments.values());
	}

	synchronized RoleMenuAssignmentResponse updateMenuAssignment(String roleCode, UpdateMenuAssignmentRequest request) {
		String roleName = switch (roleCode) {
			case "admin" -> "系统管理员";
			case "operator" -> "模型运营";
			case "auditor" -> "审计员";
			case "user" -> "普通用户";
			default -> roleCode;
		};
		RoleMenuAssignmentResponse assignment = new RoleMenuAssignmentResponse(roleCode, roleName, List.copyOf(request.entries()));
		menuAssignments.put(roleCode, assignment);
		return assignment;
	}

	private int totalMenuEntries() {
		return menus.stream().mapToInt(menu -> menu.children().size()).sum();
	}

	private Map<String, Boolean> orderedPermissions(Map<String, Boolean> permissions) {
		Map<String, Boolean> ordered = new LinkedHashMap<>();
		for (String role : List.of("admin", "operator", "auditor", "user")) {
			ordered.put(role, permissions.getOrDefault(role, false));
		}
		return ordered;
	}

	private void seedProviders() {
		providers.add(new ModelProviderResponse(
				1,
				"OpenAI",
				"商业 API",
				"https://api.openai.com/v1",
				"已启用",
				List.of("gpt-4.1", "o4-mini", "text-embedding-3-large"),
				"平台管理员",
				78
		));
		providers.add(new ModelProviderResponse(
				2,
				"DashScope",
				"国内模型平台",
				"https://dashscope.aliyuncs.com",
				"待验证",
				List.of("qwen-max", "qwen-plus", "qwen-embedding"),
				"AI 中台",
				42
		));
		providers.add(new ModelProviderResponse(
				3,
				"Local vLLM",
				"私有化部署",
				"http://10.12.8.21:8000/v1",
				"维护中",
				List.of("Qwen3-32B", "DeepSeek-R1-Distill"),
				"基础设施",
				15
		));
	}

	private void seedUsers() {
		users.add(new AdminUserResponse(1, "陈景行", "chen.jx", "系统管理员", "平台部", "已激活"));
		users.add(new AdminUserResponse(2, "林文澈", "lin.wc", "模型运营", "AI 中台", "待审核"));
		users.add(new AdminUserResponse(3, "周若安", "zhou.ra", "普通用户", "研发效能", "已激活"));
		users.add(new AdminUserResponse(4, "赵亦宁", "zhao.yn", "审计员", "安全合规", "冻结"));
	}

	private void seedPermissions() {
		permissionMatrix.add(new PermissionMatrixRowResponse("模型服务商", orderedPermissions(Map.of("admin", true, "operator", true, "auditor", true))));
		permissionMatrix.add(new PermissionMatrixRowResponse("用户注册", orderedPermissions(Map.of("admin", true, "auditor", true))));
		permissionMatrix.add(new PermissionMatrixRowResponse("角色权限", orderedPermissions(Map.of("admin", true, "auditor", true))));
		permissionMatrix.add(new PermissionMatrixRowResponse("菜单分配", orderedPermissions(Map.of("admin", true, "operator", true, "auditor", true))));
		permissionMatrix.add(new PermissionMatrixRowResponse("调用审计", orderedPermissions(Map.of("admin", true, "operator", true, "auditor", true))));
	}

	private void seedMenus() {
		menus.add(new MenuGroupResponse("系统管理", List.of("模型服务商", "用户注册", "角色权限", "菜单分配")));
		menus.add(new MenuGroupResponse("AI 工作台", List.of("对话工作区", "提示词模板", "知识库", "调用审计")));
		menus.add(new MenuGroupResponse("运维监控", List.of("模型健康检查", "配额看板", "异常告警")));

		menuAssignments.put("admin", new RoleMenuAssignmentResponse("admin", "系统管理员", List.of("系统管理", "AI 工作台", "运维监控")));
		menuAssignments.put("operator", new RoleMenuAssignmentResponse("operator", "模型运营", List.of("系统管理", "AI 工作台", "运维监控")));
		menuAssignments.put("auditor", new RoleMenuAssignmentResponse("auditor", "审计员", List.of("系统管理", "AI 工作台", "运维监控")));
		menuAssignments.put("user", new RoleMenuAssignmentResponse("user", "普通用户", List.of("对话工作区", "提示词模板", "知识库")));
	}
}
