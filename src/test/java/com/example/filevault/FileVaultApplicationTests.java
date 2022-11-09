package com.example.filevault;

import com.example.filevault.controller.FileController;
import io.swagger.v3.oas.models.headers.Header;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class FileVaultApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private FileController fileController;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void getAllFilesViaRestTemplate() throws Exception {
        JSONObject json = new JSONObject();
        json.put("username", "admin");
        json.put("password", "admin");

        MultiValueMap<String, Object> map = new LinkedMultiValueMap<String, Object>();
        map.add("username", "admin");
        map.add("password", "admin");

        // get headers from rest template
        ResponseEntity<String> responseToToken = this.restTemplate.postForEntity("/login", json.toString(), String.class);
        HttpHeaders headers = responseToToken.getHeaders();
        var token = headers.get("Authorization").get(0);

        // create headers
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Authorization", token);

        HttpEntity request = new HttpEntity(headers);

        ResponseEntity<String> response = restTemplate.exchange(
                "http://localhost:" + port + "/api/file",
                HttpMethod.GET,
                request,
                String.class,
                1
        );

        // assert
        assert(response.getStatusCodeValue() == 200);
        System.out.println(response.getBody());
    }

    @Test
    void uploadTest() throws Exception {
        JSONObject json = new JSONObject();
        json.put("username", "admin");
        json.put("password", "admin");
        ResultActions result = mockMvc.perform(post("/login")
                .content(json.toString()));
        result.andExpect(status().isOk());
        String token = result.andReturn().getResponse().getHeader("Authorization");

        MockMultipartFile firstFile = new MockMultipartFile("file", "filename.txt", "text/plain", "some text".getBytes());

//        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        ResultActions perform = mockMvc.perform(MockMvcRequestBuilders
                .multipart("/api/file")
                .file(firstFile)
                .param("comment", "test")
                .header("Authorization", token)
        );

        System.out.println("Perform");
        System.out.println(perform.andReturn().getResponse().getContentAsString());
    }

    @Test
    void loginTest() throws Exception {
        JSONObject json = new JSONObject();
        json.put("username", "admin");
        json.put("password", "admin");
        ResultActions result = mockMvc.perform(post("/login")
                .content(json.toString()));
        result.andExpect(status().isOk());

        String token = result.andReturn().getResponse().getHeader("Authorization");
    }


    @Test
    void getAllFileJson() throws Exception {
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
