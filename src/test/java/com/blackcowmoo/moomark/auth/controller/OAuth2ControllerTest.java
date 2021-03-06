package com.blackcowmoo.moomark.auth.controller;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import com.blackcowmoo.moomark.auth.model.oauth2.Token;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.shaded.json.JSONObject;

@SpringBootTest
@AutoConfigureMockMvc
public class OAuth2ControllerTest {
  @Autowired
  private MockMvc mvc;

  @Autowired
  private ObjectMapper mapper;

  @Test
  public void testGoogleCode() throws Exception {
    Token token = mapper
        .readValue(mvc.perform(get("/api/v1/oauth2/google").param("code", "test-1234")).andExpect(status().isOk())
            .andReturn().getResponse().getContentAsString(), Token.class);

    assertNotNull(token.getToken());
    assertNotNull(token.getRefreshToken());
  }

  @Test
  public void failRefreshToken() throws Exception {
    Token token = mapper
        .readValue(mvc.perform(get("/api/v1/oauth2/google").param("code", "test-1234")).andExpect(status().isOk())
            .andReturn().getResponse().getContentAsString(), Token.class);

    assertNotNull(token.getToken());
    assertNotNull(token.getRefreshToken());
    assertNotEquals(token.getToken(), "");
    assertNotEquals(token.getRefreshToken(), "");

    JSONObject requestParams = new JSONObject();
    requestParams.put("refreshToken", token.getToken());

    mvc.perform(post("/api/v1/oauth2/refresh").header("Content-Type", "application/json")
        .content(requestParams.toJSONString()))
        .andExpect(status().is(401))
        .andReturn().getResponse().getContentAsString();
  }

  @Test
  public void refreshToken() throws Exception {
    Token token = mapper
        .readValue(mvc.perform(get("/api/v1/oauth2/google").param("code", "test-1234")).andExpect(status().isOk())
            .andReturn().getResponse().getContentAsString(), Token.class);

    assertNotNull(token.getToken());
    assertNotNull(token.getRefreshToken());
    assertNotEquals(token.getToken(), "");
    assertNotEquals(token.getRefreshToken(), "");

    Thread.sleep(1000);

    JSONObject requestParams = new JSONObject();
    requestParams.put("refreshToken", token.getRefreshToken());

    Token newToken = mapper
        .readValue(
            mvc.perform(post("/api/v1/oauth2/refresh").header("Content-Type", "application/json")
                .content(requestParams.toJSONString())).andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString(),
            Token.class);

    assertNotNull(newToken.getToken());
    assertNotNull(newToken.getRefreshToken());
    assertNotEquals(newToken.getToken(), "");
    assertNotEquals(newToken.getRefreshToken(), "");
    assertNotEquals(token.getToken(), newToken.getToken());
    assertNotEquals(token.getRefreshToken(), newToken.getRefreshToken());
  }
}
