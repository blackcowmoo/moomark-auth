package com.blackcowmoo.moomark.auth.service;

import java.util.Optional;
import java.util.UUID;
import com.blackcowmoo.moomark.auth.exception.JpaErrorCode;
import com.blackcowmoo.moomark.auth.exception.JpaException;
import com.blackcowmoo.moomark.auth.jwt.JwtTokenProvider;
import com.blackcowmoo.moomark.auth.model.AuthProvider;
import com.blackcowmoo.moomark.auth.model.Role;
import com.blackcowmoo.moomark.auth.model.entity.User;
import com.blackcowmoo.moomark.auth.repository.UserRepository;
import com.blackcowmoo.moomark.auth.util.JsonBuilderUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile("development")
public class NormalUserServiceImpl implements UserService {
  @Autowired
  private UserRepository userRepository;

  @Autowired
  private JwtTokenProvider jwtTokenProvider;

  private static final int ACCESS = 0;
  private static final int REFRESH = 1;

  @Override
  public User getUserById(long id) {
    return userRepository.getOne(id);
  }

  @Override
  public Optional<User> login(String email, String password, AuthProvider authProvider) {
    return null;
  }

  @Override
  public User signUp(User user) throws Exception {
    if (userRepository.findByEmail(user.getEmail()).isPresent()) {
      throw new JpaException(JpaErrorCode.ALREADY_EXIST_EMAIL.getMsg(),
          JpaErrorCode.ALREADY_EXIST_EMAIL.getCode());
    }

    return userRepository.save(user);
  }

  @Override
  public void updateUser(User user) {
    // TODO Auto-generated method stub
  }

  @Override
  public boolean updateUserNickname(long userId, String nickname) {
    // TODO Auto-generated method stub
    return false;
  }

  public User loadUserByEmail(String email) throws JpaException {
    return userRepository.findByEmail(email)
        .orElseThrow(() -> new JpaException("Cannot find user by email"));
  }

  @Override
  public User findByName(String name) throws JpaException {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public User findByEmail(String email) throws JpaException {
    // TODO Auto-generated method stub
    return null;
  }

  public String refreshUserToken(String email, String refreshToken) throws Exception {
    User user = userRepository.findByEmail(email)
        .orElseThrow(() -> new Exception("Can't find user by email"));
    checkJwtValidStatus(user.getRefreshTokenValue(), refreshToken);
    String[] jwtTokens = createJwtTokens(user, user.getRole());
    return buildRefreshUserTokensJsonResponse(email, jwtTokens);
  }

  private void saveRefreshToken(User user, String refreshTokenValue) {
    user.setRefreshTokenValue(refreshTokenValue);
    userRepository.save(user);
  }

  private String[] createJwtTokens(User user, Role role) {
    String accessToken = jwtTokenProvider.createJwtAccessToken(user.getEmail(), role);
    String refreshTokenValue = UUID.randomUUID().toString().replace("-", "");
    saveRefreshToken(user, refreshTokenValue);
    String refreshToken = jwtTokenProvider.createJwtRefreshToken(refreshTokenValue);

    return new String[] {accessToken, refreshToken};
  }

  private void checkJwtValidStatus(String requireToken, String givenToken) throws Exception {
    String givenValue =
        String.valueOf(jwtTokenProvider.getClaimsFromJwtToken(givenToken).getBody().get("value"));
    if (!requireToken.equals(givenValue)) {
      throw new Exception("Invalid token.");
    }
  }

  private String buildRefreshUserTokensJsonResponse(String userId, String[] jwtTokens) {
    return JsonBuilderUtils.buildJsonWithHeaderAndPayload(
        JsonBuilderUtils.buildResponseHeader("RefreshTokensResponse", userId),
        JsonBuilderUtils.buildResponsePayloadFromText(new String[] {"accessToken", "refreshToken"},
            new String[] {jwtTokens[ACCESS], jwtTokens[REFRESH]}));
  }


}
