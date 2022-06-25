package com.blackcowmoo.moomark.auth.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class PassportService {
  @Value("${passport.public-key}")
  private String publicKey;

  @Value("${passport.private-key}")
  private String privateKey;

  public String getPublicKey() {
    return publicKey;
  }
}
