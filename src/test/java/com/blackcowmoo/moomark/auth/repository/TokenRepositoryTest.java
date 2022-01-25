package com.blackcowmoo.moomark.auth.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import java.time.LocalDateTime;
import java.util.UUID;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import com.blackcowmoo.moomark.auth.exception.JwtErrorCode;
import com.blackcowmoo.moomark.auth.jwt.JwtTokenProvider;
import com.blackcowmoo.moomark.auth.model.entity.Token;
import io.jsonwebtoken.JwtException;


@SpringBootTest
@RunWith(SpringRunner.class)
public class TokenRepositoryTest {

  @Autowired
  private TokenRepository tokenRepository;

  @Autowired
  private JwtTokenProvider jwtTokenProvider;

  @After
  public void tearDown() throws Exception {
    tokenRepository.deleteAll();
  }

  @Test
  public void saveTokenTest() {
    String jwtTokenValue =
        jwtTokenProvider.createJwtRefreshToken(UUID.randomUUID().toString().replace("-", ""));
    String refreshToken = jwtTokenProvider.createJwtRefreshToken(jwtTokenValue);
    String email = "test@tester.com";
    Token token =
        Token.builder().id(refreshToken).userId(email).issueTime(LocalDateTime.now()).build();

    tokenRepository.save(token);

    Token savedToken = tokenRepository.findById(refreshToken)
        .orElseThrow(() -> new JwtException(JwtErrorCode.DONT_KNOW_ERROR.getMsg()));

    assertEquals(token.getId(), savedToken.getId());
    assertEquals(token.getUserId(), savedToken.getUserId());
    assertEquals(token.getIssueTime(), savedToken.getIssueTime());
  }

  
  @Test
  public void updateTokenTest() {
    String jwtTokenValue =
        jwtTokenProvider.createJwtRefreshToken(UUID.randomUUID().toString().replace("-", ""));
    String refreshToken = jwtTokenProvider.createJwtRefreshToken(jwtTokenValue);
    String email = "test@tester.com";
    Token token =
        Token.builder().id(refreshToken).userId(email).issueTime(LocalDateTime.now()).build();

    tokenRepository.save(token);
    
    Token savedToken = tokenRepository.findById(refreshToken).get();
    savedToken.refreshToken(email, savedToken.getIssueTime().plusHours(1));
    tokenRepository.save(savedToken);
    
    Token updatedToken = tokenRepository.findById(refreshToken).get();
    assertEquals(token.getIssueTime().plusHours(1), updatedToken.getIssueTime());
    
  }
}
