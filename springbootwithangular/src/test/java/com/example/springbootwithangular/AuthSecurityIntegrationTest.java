package com.example.springbootwithangular;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
class AuthSecurityIntegrationTest {
	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Test
	void registeredUsersCanReadCatalogButCannotChangeIt() throws Exception {
		String email = "user-" + UUID.randomUUID() + "@example.com";
		MvcResult registration = mockMvc.perform(post("/api/auth/register")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(
								java.util.Map.of("email", email, "password", "example-password-123"))))
				.andExpect(status().isOk())
				.andReturn();
		JsonNode response = objectMapper.readTree(registration.getResponse().getContentAsString());
		String token = response.get("token").asText();

		mockMvc.perform(get("/api/products"))
				.andExpect(status().isOk());

		mockMvc.perform(post("/api/products")
						.header("Authorization", "Bearer " + token)
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{"name":"Sample product","description":"Example","price":10,"category":"Home"}
								"""))
				.andExpect(status().isForbidden());
	}
}
