package com.blackcowmoo.moomark.auth.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import com.blackcowmoo.moomark.auth.model.AuthProvider;
import com.blackcowmoo.moomark.auth.model.entity.User;
import com.blackcowmoo.moomark.auth.model.oauth2.Token;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.shaded.json.JSONObject;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

@TestMethodOrder(value = MethodOrderer.OrderAnnotation.class)
@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {
  @Autowired
  private MockMvc mvc;

  @Autowired
  private ObjectMapper mapper;

  @Test
  @Order(1)
  public void me() throws Exception {
    Token token = mapper
        .readValue(mvc.perform(get("/api/v1/oauth2/google").param("code", "test-1234")).andExpect(status().isOk())
            .andReturn().getResponse().getContentAsString(), Token.class);

    assertNotNull(token.getToken());

    User user = mapper
        .readValue(mvc.perform(get("/api/v1/user").header("Authorization", token.getToken()))
            .andExpect(status().isOk())
            .andReturn().getResponse().getContentAsString(), User.class);

    assertEquals(user.getAuthProvider(), AuthProvider.TEST);
    assertEquals(user.getId(), "1234");
  }

  @Test
  @Order(2)
  public void user() throws Exception {
    Token token = mapper
        .readValue(mvc.perform(get("/api/v1/oauth2/google").param("code", "test-test")).andExpect(status().isOk())
            .andReturn().getResponse().getContentAsString(), Token.class);

    assertNotNull(token.getToken());

    User user = mapper
        .readValue(mvc.perform(get("/api/v1/user/TEST/test").header("Content-Type", "application/json"))
            .andExpect(status().isOk()).andReturn().getResponse()
            .getContentAsString(), User.class);

    assertEquals(user.getAuthProvider(), AuthProvider.TEST);
    assertEquals(user.getId(), "test");
  }

  @Test
  @Order(3)
  public void modifyUser() throws Exception {
    String id = "test";
    Token token = mapper
        .readValue(mvc.perform(get("/api/v1/oauth2/google").param("code", "test-" + id)).andExpect(status().isOk())
            .andReturn().getResponse().getContentAsString(), Token.class);

    assertNotNull(token.getToken());

    User beforeUser = mapper
        .readValue(mvc.perform(get("/api/v1/user/TEST/test").header("Content-Type", "application/json"))
            .andExpect(status().isOk()).andReturn().getResponse()
            .getContentAsString(), User.class);

    assertEquals(beforeUser.getAuthProvider(), AuthProvider.TEST);
    assertEquals(beforeUser.getId(), id);
    assertNotEquals(beforeUser.getNickname(), "");
    assertNotEquals(beforeUser.getPicture(), "");

    JSONObject requestParams1 = new JSONObject();
    requestParams1.put("nickname", null);
    requestParams1.put("picture", null);

    User user1 = mapper
        .readValue(mvc.perform(post("/api/v1/user")
            .header("Content-Type", "application/json")
            .header("Authorization", token.getToken())
            .content(requestParams1.toJSONString()))
            .andExpect(status().isOk()).andReturn().getResponse()
            .getContentAsString(), User.class);

    assertEquals(user1.getAuthProvider(), AuthProvider.TEST);
    assertEquals(user1.getId(), id);
    assertEquals(beforeUser.getNickname(), beforeUser.getNickname());
    assertEquals(beforeUser.getPicture(), beforeUser.getPicture());
  }

  @Test
  @Order(Integer.MAX_VALUE)
  public void withdraw() throws Exception {
    Token token = mapper
        .readValue(mvc.perform(get("/api/v1/oauth2/google").param("code", "test-1234")).andExpect(status().isOk())
            .andReturn().getResponse().getContentAsString(), Token.class);

    assertNotNull(token.getToken());
    assertNotNull(token.getRefreshToken());
  }

}
