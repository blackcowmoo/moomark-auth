package com.blackcowmoo.moomark.auth.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import com.blackcowmoo.moomark.auth.model.entity.User;
import com.blackcowmoo.moomark.auth.model.oauth2.Token;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
public class PssportControllerTest {
  @Value("${passport.public-key}")
  private String passportPublicKey;

  @Value("${passport.test.token.expired}")
  private String expiredTestPassport;

  @Autowired
  private MockMvc mvc;

  @Autowired
  private ObjectMapper mapper;

  @Test
  public void generatePassport() throws Exception {
    String userId = "1234";
    Token token = mapper
        .readValue(mvc.perform(get("/api/v1/oauth2/google").param("code", "test-" + userId)).andExpect(status().isOk())
            .andReturn().getResponse().getContentAsString(), Token.class);

    assertNotNull(token.getToken());

    String passport = mvc.perform(get("/api/v1/passport").header("Authorization", token.getToken()))
        .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();

    User user = mapper
        .readValue(mvc.perform(get("/api/v1/passport/verify").header("x-moom-passport", passport))
            .andExpect(status().isOk()).andReturn().getResponse().getContentAsString(), User.class);

    assertEquals(user.getId(), userId);
  }

  @Test
  public void verifyPassport() throws Exception {
    String userId = "1234";
    Token token = mapper
        .readValue(mvc.perform(get("/api/v1/oauth2/google").param("code", "test-" + userId)).andExpect(status().isOk())
            .andReturn().getResponse().getContentAsString(), Token.class);

    assertNotNull(token.getToken());

    String passport = mvc.perform(get("/api/v1/passport").header("Authorization", token.getToken()))
        .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();

    User user = mapper
        .readValue(mvc.perform(get("/api/v1/user").header("x-moom-passport", passport))
            .andExpect(status().isOk()).andReturn().getResponse().getContentAsString(), User.class);

    assertEquals(user.getId(), userId);
  }

  @Test
  public void checkPublicKey() throws Exception {
    String publicKey = passportPublicKey;
    String testPublicKey = mvc.perform(get("/api/v1/passport/verify/public"))
        .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();

    assertNotNull(publicKey);
    assertNotEquals(publicKey, "");
    assertEquals(publicKey, testPublicKey);
  }

  @Test
  public void expiredPassport() throws Exception {
    String response = mvc.perform(get("/api/v1/passport/verify").header("x-moom-passport", expiredTestPassport))
        .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();

    assertEquals(response, "");
  }
}
