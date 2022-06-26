package com.blackcowmoo.moomark.auth.service;

import java.util.Base64;

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

  private RsaUtil rsaUtil;

  @PostConstruct
  public void buildRsaKeys() throws Exception {
    rsaUtil = new RsaUtil(publicKeyString, privateKeyString);
  }

  public String getPublicKeyString() {
    return publicKeyString;
  }

  public User parsePassport(String passport) {
    try {
      return mapper.readValue(rsaUtil.decryptByPublicKey(Base64.getDecoder().decode(passport)), User.class);
    } catch (Exception e) {
      log.error("parsePassport", e);
    }

    return null;
  }

  public String generatePassport(User user) {
    try {
      String userString = mapper.writeValueAsString(user);
      return Base64.getEncoder().encodeToString(rsaUtil.encryptByPrivateKey(userString));
    } catch (Exception e) {
      log.error("generatePassport", e);
    }

    return "";
  }
}
