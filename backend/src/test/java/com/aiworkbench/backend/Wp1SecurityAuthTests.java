package com.aiworkbench.backend;

import com.aiworkbench.backend.entity.SysUser;
import com.aiworkbench.backend.repository.SysUserRepository;
import com.aiworkbench.backend.security.JwtUtils;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * WP1 安全与认证修复回归测试（BUG-001 / BUG-007 / BUG-008 / BUG-021）
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class Wp1SecurityAuthTests {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private SysUserRepository sysUserRepository;

	@Autowired
	private JwtUtils jwtUtils;

	private final ObjectMapper objectMapper = new ObjectMapper();

	private String loginAsAdmin() throws Exception {
		MvcResult result = mockMvc.perform(post("/api/v1/auth/login")
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{"username": "admin", "password": "admin123"}
								"""))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.data.token").isNotEmpty())
				.andReturn();
		JsonNode json = objectMapper.readTree(result.getResponse().getContentAsString());
		return json.path("data").path("token").asText();
	}

	// --- BUG-001: admin GET 端点不再 permitAll ---

	@Test
	void anonymousAdminGetIsRejectedWith401Contract() throws Exception {
		mockMvc.perform(get("/api/v1/admin/overview"))
				.andExpect(status().isUnauthorized())
				.andExpect(jsonPath("$.code").value(401))
				.andExpect(jsonPath("$.message").isNotEmpty());
	}

	@Test
	void authenticatedAdminGetReturnsOverview() throws Exception {
		String token = loginAsAdmin();

		mockMvc.perform(get("/api/v1/admin/overview")
						.header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.metrics").isArray());
	}

	// --- BUG-007: 注册分配默认 user 角色 ---

	@Test
	void registerAssignsDefaultUserRoleAndTokenClaim() throws Exception {
		MvcResult result = mockMvc.perform(post("/api/v1/auth/register")
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{"username": "wp1_newbie", "password": "secret123",
								 "nickname": "新用户", "email": "newbie@example.com"}
								"""))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.data.token").isNotEmpty())
				.andReturn();
		JsonNode json = objectMapper.readTree(result.getResponse().getContentAsString());
		String token = json.path("data").path("token").asText();

		SysUser saved = sysUserRepository.findByUsername("wp1_newbie").orElseThrow();
		assertThat(saved.getRoles())
				.extracting(role -> role.getRoleCode())
				.containsExactly("user");
		assertThat(jwtUtils.parseToken(token).get("roles", java.util.List.class))
				.contains("user");
	}

	// --- BUG-008: IllegalArgumentException 返回业务提示而非 500 ---

	@Test
	void loginWithWrongPasswordReturnsBadRequestWithBusinessMessage() throws Exception {
		mockMvc.perform(post("/api/v1/auth/login")
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{"username": "admin", "password": "wrong-password"}
								"""))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.code").value(400))
				.andExpect(jsonPath("$.message").value("用户名或密码错误"));
	}

	@Test
	void loginUnknownUserReturnsBadRequestNotSystemError() throws Exception {
		mockMvc.perform(post("/api/v1/auth/login")
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{"username": "nobody", "password": "whatever"}
								"""))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.message").value("用户名或密码错误"));
	}

	// --- BUG-021: JWT 密钥缺失/过短 fail-fast，不再回退硬编码 ---

	@Test
	void jwtUtilsRejectsMissingSecret() {
		assertThatThrownBy(() -> new JwtUtils("", 3600000L))
				.isInstanceOf(IllegalStateException.class)
				.hasMessageContaining("JWT_SECRET 未配置");
	}

	@Test
	void jwtUtilsRejectsShortSecret() {
		assertThatThrownBy(() -> new JwtUtils("too-short-secret", 3600000L))
				.isInstanceOf(IllegalStateException.class)
				.hasMessageContaining("长度不足");
	}
}
