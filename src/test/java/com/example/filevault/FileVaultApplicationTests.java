package com.example.filevault;

import com.example.filevault.controller.FileController;
import netscape.javascript.JSObject;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

//@WebMvcTest(FileController.class)
@SpringBootTest
@AutoConfigureMockMvc
class FileVaultApplicationTests {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private FileController fileController;

	@Test
	void test() throws Exception {
		// create post request to get jwt token using json body
		ResultActions resultActions = mockMvc.perform(post("/login")
				.contentType("application/json")
				.content("{\"username\":\"admin\",\"password\":\"admin\"}"))
				.andExpect(status().isOk());

		// get jwt token from response from authentication header
		String token = resultActions.andReturn().getResponse().getHeader("Authorization");

		System.out.println(token);

		// create get request to get all files
		ResultActions authorization = mockMvc.perform(get("/api/file")
						.header("Authorization", token))
				.andExpect(status().isOk());

		// get all files from response get content
		String content = authorization.andReturn().getResponse().getContentAsString();

	}

}
