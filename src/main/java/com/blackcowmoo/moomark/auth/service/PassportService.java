package com.blackcowmoo.moomark.auth.service;

import java.security.PrivateKey;
import java.security.PublicKey;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.blackcowmoo.moomark.auth.model.entity.User;
import com.blackcowmoo.moomark.auth.util.RsaUtil;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class PassportService {
  @Value("${passport.public-key}")
  private String publicKeyString;

  @Value("${passport.private-key}")
  private String privateKeyString;

  @Autowired
  private ObjectMapper mapper;

  private PublicKey publicKey;
  private PrivateKey privateKey;

  @PostConstruct
  public void buildRsaKeys() throws Exception {
    publicKey = RsaUtil.buildPublicKey(publicKeyString);
    privateKey = RsaUtil.buildPrivateKey(privateKeyString);
  }

  public String getPublicKeyString() {
    return publicKeyString;
  }

  public PublicKey testPublicKey() {
    return publicKey;
  }

  public PrivateKey testPrivateKey() {
    return privateKey;
  }

  public String generatePassport(User user) {
    String userString = "";
    try {
      userString = mapper.writeValueAsString(user);
    } catch (Exception e) {
      log.error("generatePassport: Invalid user", e);
    }

    return userString;
  }
}
