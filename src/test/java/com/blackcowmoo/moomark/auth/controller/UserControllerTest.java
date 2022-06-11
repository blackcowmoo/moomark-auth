package com.blackcowmoo.moomark.auth.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import com.blackcowmoo.moomark.auth.model.AuthProvider;
import com.blackcowmoo.moomark.auth.model.entity.User;
import com.blackcowmoo.moomark.auth.model.oauth2.Token;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {
  @Autowired
  private MockMvc mvc;

  @Autowired
  private ObjectMapper mapper;

  @Test
  public void withdraw() throws Exception {
    Token token = mapper
        .readValue(mvc.perform(get("/api/v1/oauth2/google").param("code", "test-1234")).andExpect(status().isOk())
            .andReturn().getResponse().getContentAsString(), Token.class);

    assertNotNull(token.getToken());
    assertNotNull(token.getRefreshToken());
  }

  @Test
  public void me() throws Exception {
    Token token = mapper
        .readValue(mvc.perform(get("/api/v1/oauth2/google").param("code", "test-1234")).andExpect(status().isOk())
            .andReturn().getResponse().getContentAsString(), Token.class);

    assertNotNull(token.getToken());

    User user = mapper
        .readValue(mvc.perform(get("/api/v1/oauth2/google").header("Authorization", token.getToken()))
            .andExpect(status().isOk())
            .andReturn().getResponse().getContentAsString(), User.class);

    assertEquals(user.getAuthProvider(), AuthProvider.TEST);
    assertEquals(user.getId(), "1234");
  }
}
