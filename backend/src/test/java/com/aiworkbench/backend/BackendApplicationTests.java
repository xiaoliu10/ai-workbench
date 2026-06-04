package com.aiworkbench.backend;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class BackendApplicationTests {

	@Autowired
	private MockMvc mockMvc;

	@Test
	void contextLoads() {
	}

	@Test
	void returnsAdminOverview() throws Exception {
		mockMvc.perform(get("/api/admin/overview"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.metrics", hasSize(4)))
				.andExpect(jsonPath("$.health", hasSize(4)));
	}

	@Test
	void createsModelProvider() throws Exception {
		mockMvc.perform(post("/api/admin/model-providers")
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
		mockMvc.perform(put("/api/admin/menu-assignments/user")
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
