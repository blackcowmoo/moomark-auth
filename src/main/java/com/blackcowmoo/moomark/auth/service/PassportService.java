package com.blackcowmoo.moomark.auth.service;

import com.blackcowmoo.moomark.auth.model.dto.Passport;
import com.blackcowmoo.moomark.auth.model.entity.PassportKey;
import com.blackcowmoo.moomark.auth.repository.PassportKeyRedisRepository;
import com.blackcowmoo.moomark.auth.util.HashUtils;
import io.micrometer.core.instrument.util.StringUtils;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Base64;

import javax.annotation.PostConstruct;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;

import com.blackcowmoo.moomark.auth.model.AuthProvider;
import com.blackcowmoo.moomark.auth.model.dto.PassportResponse;
import com.blackcowmoo.moomark.auth.util.AesUtil;
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

  private Long passportExpireSeconds = 120L;

  private final ObjectMapper mapper;
  private final PassportKeyRedisRepository passportKeyRedisRepository;

  private Base64.Encoder encoder = Base64.getEncoder();
  private Base64.Decoder decoder = Base64.getDecoder();

  private RsaUtil rsaUtil;

  private final AesUtil aesUtil;

  public PassportService(ObjectMapper mapper, PassportKeyRedisRepository passportKeyRedisRepository, AesUtil aesUtil) {
    this.mapper = mapper;
    this.passportKeyRedisRepository = passportKeyRedisRepository;
    this.aesUtil = aesUtil;
  }

  @PostConstruct
  public void buildRsaKeys() throws Exception {
    rsaUtil = new RsaUtil(publicKeyString, privateKeyString);
  }

  public String getPublicKeyString() {
    return publicKeyString;
  }

  public User parsePassport(String passport, String passportKey) {
    try {
      Passport passportResult = decryptPassport(passportKey);
      if (passportResult.getExp().after(Timestamp.valueOf(LocalDateTime.now()))) {
        String hash = passportResult.getHash();
        SecretKey key = new SecretKeySpec(decoder.decode(passportResult.getKey()), "AES");
        String userBody = aesUtil.decrypt(decoder.decode(passport), key);
        if (getHash(userBody).equals(hash)) {
          return mapper.readValue(decoder.decode(userBody), User.class);
        }
      }
    } catch (Exception e) {
      log.error(e.getMessage(), e);
    }

    return null;
  }

  public PassportResponse generatePassport(User user) {
    try {
      SecretKey key = getAesKey(user.getAuthProvider(), user.getId());
      String userBody = encoder.encodeToString(mapper.writeValueAsString(user).getBytes());

      Passport passport = new Passport();
      passport.setExp(Timestamp.valueOf(LocalDateTime.now().plusSeconds((passportExpireSeconds))));
      passport.setKey(encoder.encodeToString(key.getEncoded()));
      passport.setHash(getHash(userBody));

      PassportResponse response = new PassportResponse();
      response.setPassport(encoder.encodeToString(aesUtil.encrypt(userBody, key)));
      response.setKey(encryptPassport(passport));
      return response;
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      return null;
    }
  }

  private String getHash(String user) throws Exception {
    MessageDigest md = MessageDigest.getInstance("MD5");
    byte[] digest = md.digest(user.getBytes(StandardCharsets.UTF_8));
    return DatatypeConverter.printHexBinary(digest);
  }

  private String encryptPassport(Passport passport) throws Exception {
    return encoder.encodeToString(rsaUtil.encryptByPrivateKey(mapper.writeValueAsString(passport)));
  }

  private Passport decryptPassport(String passport) throws Exception {
    return mapper.readValue(rsaUtil.decryptByPublicKey(decoder.decode(passport)), Passport.class);
  }

  private SecretKey getAesKey(AuthProvider provider, String id) {
    String providerValue = provider.getValue();
    String hash = makeKeyHash(providerValue, id);
    PassportKey savedPassportKey = passportKeyRedisRepository.findById(hash)
      .orElseGet(() -> saveNewPassportKey(hash));

    return new SecretKeySpec(savedPassportKey.getKey(), "AES");
  }

  private PassportKey saveNewPassportKey(String hash) {
    SecretKey key = aesUtil.generateNewKey();
    PassportKey passportKey = PassportKey.builder()
      .hash(hash)
      .key(key.getEncoded())
      .build();

    passportKeyRedisRepository.save(passportKey);
    return passportKey;
  }

  private String makeKeyHash(String providerValue, String id) {
    // TODO : We need to develop more complexity method
    if (StringUtils.isBlank(providerValue) || StringUtils.isBlank(id)) {
      throw new IllegalArgumentException("ProviderValue or ID must be not null");
    }
    return HashUtils.toSha256(providerValue + id);
  }

}
