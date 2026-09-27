package com.aiworkbench.backend;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class BackendApplicationTests {

	@Autowired
	private MockMvc mockMvc;

	private final ObjectMapper objectMapper = new ObjectMapper();

	/**
	 * WP1 修复后 /api/admin/** 全部要求认证，统一以 admin 账号登录取 token。
	 */
	private String adminToken() throws Exception {
		var result = mockMvc.perform(post("/api/v1/auth/login")
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{"username": "admin", "password": "admin123"}
								"""))
				.andExpect(status().isOk())
				.andReturn();
		return objectMapper.readTree(result.getResponse().getContentAsString())
				.path("data").path("token").asText();
	}

	@Test
	void contextLoads() {
	}

	@Test
	void returnsAdminOverview() throws Exception {
		mockMvc.perform(get("/api/v1/admin/overview")
						.header(HttpHeaders.AUTHORIZATION, "Bearer " + adminToken()))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.metrics", hasSize(4)))
				.andExpect(jsonPath("$.health", hasSize(4)));
	}

	@Test
	void createsModelProvider() throws Exception {
		mockMvc.perform(post("/api/v1/admin/model-providers")
						.header(HttpHeaders.AUTHORIZATION, "Bearer " + adminToken())
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{
								  "name": "Anthropic",
								  "type": "商业 API",
								  "endpoint": "https://api.anthropic.com/v1",
								  "owner": "AI 中台",
								  "models": ["claude-sonnet-4"]
								}
								"""))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.name").value("Anthropic"))
				.andExpect(jsonPath("$.status").value("待验证"));
	}

	@Test
	void updatesMenuAssignment() throws Exception {
		mockMvc.perform(put("/api/v1/admin/menu-assignments/user")
						.header(HttpHeaders.AUTHORIZATION, "Bearer " + adminToken())
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{
								  "entries": ["对话工作区", "知识库"]
								}
								"""))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.roleCode").value("user"))
				.andExpect(jsonPath("$.entries", hasSize(2)));
	}
}
