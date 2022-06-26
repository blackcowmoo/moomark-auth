package com.blackcowmoo.moomark.auth.service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Base64;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.blackcowmoo.moomark.auth.model.dto.PassportResponse;
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

  private Long passportTTL = 120L;

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
      PassportResponse passportResult = mapper
          .readValue(rsaUtil.decryptByPublicKey(Base64.getDecoder().decode(passport)), PassportResponse.class);

      if (passportResult.getExp().after(Timestamp.valueOf(LocalDateTime.now()))) {
        return passportResult.getUser();
      }
    } catch (Exception e) {
      log.error("parsePassport", e);
    }

    return null;
  }

  public String generatePassport(User user) {
    try {
      PassportResponse passport = new PassportResponse();
      passport.setExp(Timestamp.valueOf(LocalDateTime.now().plusSeconds((passportTTL))));
      passport.setUser(user);

      return Base64.getEncoder().encodeToString(rsaUtil.encryptByPrivateKey(mapper.writeValueAsString(passport)));
    } catch (Exception e) {
      log.error("generatePassport", e);
    }

    return "";
  }
}
