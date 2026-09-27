package com.aiworkbench.backend.admin;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin")
@CrossOrigin(origins = "http://localhost:3000")
class AdminController {

	private final AdminConfigurationService adminConfigurationService;

	AdminController(AdminConfigurationService adminConfigurationService) {
		this.adminConfigurationService = adminConfigurationService;
	}

	@GetMapping("/overview")
	AdminOverviewResponse overview() {
		return adminConfigurationService.overview();
	}

	@GetMapping("/model-providers")
	List<ModelProviderResponse> modelProviders() {
		return adminConfigurationService.listProviders();
	}

	@PostMapping("/model-providers")
	@ResponseStatus(HttpStatus.CREATED)
	ModelProviderResponse createModelProvider(@Valid @RequestBody CreateModelProviderRequest request) {
		return adminConfigurationService.createProvider(request);
	}

	@GetMapping("/registration-policy")
	RegistrationPolicyResponse registrationPolicy() {
		return adminConfigurationService.registrationPolicy();
	}

	@PutMapping("/registration-policy")
	RegistrationPolicyResponse updateRegistrationPolicy(@Valid @RequestBody UpdateRegistrationPolicyRequest request) {
		return adminConfigurationService.updateRegistrationPolicy(request);
	}

	@GetMapping("/users")
	List<AdminUserResponse> users() {
		return adminConfigurationService.listUsers();
	}

	@PostMapping("/users")
	@ResponseStatus(HttpStatus.CREATED)
	AdminUserResponse registerUser(@Valid @RequestBody RegisterAdminUserRequest request) {
		return adminConfigurationService.registerUser(request);
	}

	@GetMapping("/permissions")
	List<PermissionMatrixRowResponse> permissions() {
		return adminConfigurationService.permissionMatrix();
	}

	@PutMapping("/permissions/{module}")
	PermissionMatrixRowResponse updatePermission(
			@PathVariable String module,
			@Valid @RequestBody UpdateRolePermissionRequest request
	) {
		return adminConfigurationService.updateRolePermission(module, request);
	}

	@GetMapping("/menus")
	List<MenuGroupResponse> menus() {
		return adminConfigurationService.menus();
	}

	@GetMapping("/menu-assignments")
	List<RoleMenuAssignmentResponse> menuAssignments() {
		return adminConfigurationService.menuAssignments();
	}

	@PutMapping("/menu-assignments/{roleCode}")
	RoleMenuAssignmentResponse updateMenuAssignment(
			@PathVariable String roleCode,
			@Valid @RequestBody UpdateMenuAssignmentRequest request
	) {
		return adminConfigurationService.updateMenuAssignment(roleCode, request);
	}
}
