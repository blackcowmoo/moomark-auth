package com.blackcowmoo.moomark.auth.controller;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import com.blackcowmoo.moomark.auth.model.oauth2.Token;
import com.fasterxml.jackson.databind.ObjectMapper;

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
  public void refreshToken() throws Exception {
    Token token = mapper
        .readValue(mvc.perform(get("/api/v1/oauth2/google").param("code", "test-1234")).andExpect(status().isOk())
            .andReturn().getResponse().getContentAsString(), Token.class);

    assertNotNull(token.getToken());
    assertNotNull(token.getRefreshToken());

    Token newToken = mapper
        .readValue(mvc.perform(get("/api/v1/oauth2/google").param("code", "test-1234")).andExpect(status().isOk())
            .andReturn().getResponse().getContentAsString(), Token.class);

    assertNotNull(newToken.getToken());
    assertNotEquals(newToken.getToken(), "");
    assertNotNull(newToken.getRefreshToken());
    assertNotNull(newToken.getRefreshToken(), "");

  }
}
