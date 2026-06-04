package com.aiworkbench.backend.admin;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.Map;

record AdminOverviewResponse(
		List<AdminMetricResponse> metrics,
		List<SystemHealthResponse> health
) {
}

record AdminMetricResponse(
		String label,
		String value,
		String note
) {
}

record SystemHealthResponse(
		String name,
		String status,
		String note
) {
}

record ModelProviderResponse(
		long id,
		String name,
		String type,
		String endpoint,
		String status,
		List<String> models,
		String owner,
		int quotaPercent
) {
}

record CreateModelProviderRequest(
		@NotBlank String name,
		@NotBlank String type,
		@NotBlank String endpoint,
		@NotBlank String owner,
		@NotEmpty List<@NotBlank String> models
) {
}

record AdminUserResponse(
		long id,
		String name,
		String account,
		String role,
		String org,
		String status
) {
}

record RegisterAdminUserRequest(
		@NotBlank String name,
		@NotBlank String account,
		@NotBlank String role,
		@NotBlank String org
) {
}

record RegistrationPolicyResponse(
		String defaultOrg,
		String defaultRole,
		String approvalMode,
		String emailDomainAllowlist
) {
}

record UpdateRegistrationPolicyRequest(
		@NotBlank String defaultOrg,
		@NotBlank String defaultRole,
		@NotBlank String approvalMode,
		@NotBlank String emailDomainAllowlist
) {
}

record PermissionMatrixRowResponse(
		String module,
		Map<String, Boolean> permissions
) {
}

record UpdateRolePermissionRequest(
		@NotNull Map<String, Boolean> permissions
) {
}

record MenuGroupResponse(
		String title,
		List<String> children
) {
}

record RoleMenuAssignmentResponse(
		String roleCode,
		String roleName,
		List<String> entries
) {
}

record UpdateMenuAssignmentRequest(
		@NotEmpty List<@NotBlank String> entries
) {
}
