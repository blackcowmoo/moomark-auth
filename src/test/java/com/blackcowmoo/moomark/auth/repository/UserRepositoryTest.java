package com.blackcowmoo.moomark.auth.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import com.blackcowmoo.moomark.auth.exception.JpaErrorCode;
import com.blackcowmoo.moomark.auth.exception.JpaException;
import com.blackcowmoo.moomark.auth.model.AuthProvider;
import com.blackcowmoo.moomark.auth.model.Role;
import com.blackcowmoo.moomark.auth.model.entity.User;


@SpringBootTest
@RunWith(SpringRunner.class)
@AutoConfigureTestDatabase(replace = Replace.NONE)
public class UserRepositoryTest {

  @Autowired
  private UserRepository userRepository;

  @Test
  @DisplayName("멤버가 잘 저장이 되는지 확인")
  public void saveUserTest() {

    User user = User.builder().authProvider(AuthProvider.GOOGLE).email("test@test.com")
        .name("Hong gil dong").nickname("Apisapple").role(Role.USER).build();

    userRepository.save(user);
    User savedUser = userRepository.save(user);

    assertEquals(user.getAuthProvider(), savedUser.getAuthProvider());
    assertEquals(user.getEmail(), savedUser.getEmail());
    assertEquals(user.getName(), savedUser.getName());
    assertEquals(user.getNickname(), savedUser.getNickname());
    assertEquals(user.getRole(), savedUser.getRole());
  }

  @Test
  @DisplayName("멤버를 잘 찾는지 확인하는 함수")
  public void findUserTest() throws JpaException {

    User savedUser =
        userRepository.save(User.builder().authProvider(AuthProvider.GOOGLE).email("test@test.com")
            .name("Hong gil dong").nickname("Apisapple").role(Role.USER).build());


    User findUser = userRepository.findById(savedUser.getId())
        .orElseThrow(() -> new JpaException(JpaErrorCode.CANNOT_FIND_MEMBER.getMsg(),
            JpaErrorCode.CANNOT_FIND_MEMBER.getCode()));

    assertEquals(savedUser.getAuthProvider(), findUser.getAuthProvider());
    assertEquals(savedUser.getEmail(), findUser.getEmail());
    assertEquals(savedUser.getName(), findUser.getName());
    assertEquals(savedUser.getNickname(), findUser.getNickname());
    assertEquals(savedUser.getRole(), findUser.getRole());
  }
}
