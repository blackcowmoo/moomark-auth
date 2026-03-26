package com.blackcowmoo.moomark.auth.service

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.any
import org.mockito.Mockito.`when`
import org.mockito.Mockito.doNothing
import org.mockito.Mockito.verify
import com.blackcowmoo.moomark.auth.model.AuthProvider
import com.blackcowmoo.moomark.auth.model.Role
import com.blackcowmoo.moomark.auth.repository.UserRepository

class UserServiceTest {

  private lateinit var userRepository: UserRepository
  private lateinit var userService: UserService

  @BeforeEach
  fun setUp() {
    userRepository = mock(UserRepository::class.java)
    userService = UserService()

    val defaultPicture = "https://default.com/picture.jpg"
    val field = UserService::class.java.getDeclaredField("defaultPicture")
    field.isAccessible = true
    field.set(userService, defaultPicture)

    val userRepositoryField = UserService::class.java.getDeclaredField("userRepository")
    userRepositoryField.isAccessible = true
    userRepositoryField.set(userService, userRepository)
  }

  @Test
  fun `getUserById should return user when exists`() {
    val id = "test-id"
    val provider = AuthProvider.GOOGLE
    val user = User(id, provider, "test@test.com", "test", "https://test.com", Role.USER)

    `when`(userRepository.findByIdAndAuthProvider(id, provider)).thenReturn(user)

    val result = userService.getUserById(provider, id)

    assertNotNull(result)
    assertEquals(id, result.id)
    assertEquals(provider, result.authProvider)
  }

  @Test
  fun `getUserById should return null when user does not exist`() {
    val id = "non-existent-id"
    val provider = AuthProvider.GOOGLE

    `when`(userRepository.findByIdAndAuthProvider(id, provider)).thenReturn(null)

    val result = userService.getUserById(provider, id)

    assertNull(result)
  }

  @Test
  fun `signUp should save new user`() {
    val id = "test-id"
    val provider = AuthProvider.GOOGLE
    val nickname = "testuser"
    val email = "test@test.com"
    val picture = "https://test.com/picture.jpg"

    `when`(userRepository.save(any())).thenAnswer { invocation ->
      invocation.getArgument<User>(0)
    }

    val user = userService.signUp(id, provider, nickname, email, picture)

    assertNotNull(user)
    assertEquals(id, user.id)
    assertEquals(provider, user.authProvider)
    assertEquals(nickname, user.nickname)
    assertEquals(email, user.email)
    assertEquals(picture, user.picture)
    assertEquals(Role.USER, user.role)
  }

  @Test
  fun `withdraw with id and provider should delete user`() {
    val id = "test-id"
    val provider = AuthProvider.GOOGLE
    val user = User(id, provider, "test@test.com", "test", "https://test.com", Role.USER)

    `when`(userRepository.findByIdAndAuthProvider(id, provider)).thenReturn(user)
    doNothing().`when`(userRepository).delete(user)

    userService.withdraw(id, provider)

    verify(userRepository).delete(user)
  }

  @Test
  fun `withdraw with user should delete user`() {
    val user = User("test-id", AuthProvider.GOOGLE, "test@test.com", "test", "https://test.com", Role.USER)

    doNothing().`when`(userRepository).delete(user)

    userService.withdraw(user)

    verify(userRepository).delete(user)
  }

  @Test
  fun `updateUser should update nickname when provided`() {
    val id = "test-id"
    val provider = AuthProvider.GOOGLE
    val originalUser = User(id, provider, "test@test.com", "oldNickname", "https://test.com", Role.USER)
    val newNickname = "newNickname"

    `when`(userRepository.save(originalUser)).thenAnswer { invocation ->
      invocation.getArgument<User>(0)
    }

    val updatedUser = userService.updateUser(originalUser, newNickname, null)

    assertNotNull(updatedUser)
    assertEquals(newNickname, updatedUser.nickname)
    assertEquals("https://test.com", updatedUser.picture)
  }

  @Test
  fun `updateUser should not update nickname when empty string`() {
    val id = "test-id"
    val provider = AuthProvider.GOOGLE
    val originalUser = User(id, provider, "test@test.com", "oldNickname", "https://test.com", Role.USER)
    val newNickname = ""

    `when`(userRepository.save(originalUser)).thenAnswer { invocation ->
      invocation.getArgument<User>(0)
    }

    val updatedUser = userService.updateUser(originalUser, newNickname, null)

    assertNotNull(updatedUser)
    assertEquals("oldNickname", updatedUser.nickname)
  }

  @Test
  fun `updateUser should update picture when provided`() {
    val id = "test-id"
    val provider = AuthProvider.GOOGLE
    val originalUser = User(id, provider, "test@test.com", "test", "https://old.com", Role.USER)
    val newPicture = "https://new.com/picture.jpg"

    `when`(userRepository.save(originalUser)).thenAnswer { invocation ->
      invocation.getArgument<User>(0)
    }

    val updatedUser = userService.updateUser(originalUser, null, newPicture)

    assertNotNull(updatedUser)
    assertEquals("https://old.com", updatedUser.nickname)
    assertEquals(newPicture, updatedUser.picture)
  }

  @Test
  fun `updateUser should update picture to default when empty string`() {
    val id = "test-id"
    val provider = AuthProvider.GOOGLE
    val originalUser = User(id, provider, "test@test.com", "test", "https://old.com", Role.USER)
    val newPicture = ""

    val defaultPicture = "https://default.com/picture.jpg"
    val field = UserService::class.java.getDeclaredField("defaultPicture")
    field.isAccessible = true
    field.set(userService, defaultPicture)

    `when`(userRepository.save(originalUser)).thenAnswer { invocation ->
      invocation.getArgument<User>(0)
    }

    val updatedUser = userService.updateUser(originalUser, null, newPicture)

    assertNotNull(updatedUser)
    assertEquals("https://old.com", updatedUser.nickname)
    assertEquals(defaultPicture, updatedUser.picture)
  }

  @Test
  fun `updateUser should not update picture when not https`() {
    val id = "test-id"
    val provider = AuthProvider.GOOGLE
    val originalUser = User(id, provider, "test@test.com", "test", "https://old.com", Role.USER)
    val newPicture = "http://notsecure.com/picture.jpg"

    `when`(userRepository.save(originalUser)).thenAnswer { invocation ->
      invocation.getArgument<User>(0)
    }

    val updatedUser = userService.updateUser(originalUser, null, newPicture)

    assertNotNull(updatedUser)
    assertEquals("https://old.com", updatedUser.picture)
  }

  @Test
  fun `updateUser should update both nickname and picture when both provided`() {
    val id = "test-id"
    val provider = AuthProvider.GOOGLE
    val originalUser = User(id, provider, "test@test.com", "oldNickname", "https://old.com", Role.USER)
    val newNickname = "newNickname"
    val newPicture = "https://new.com/picture.jpg"

    `when`(userRepository.save(originalUser)).thenAnswer { invocation ->
      invocation.getArgument<User>(0)
    }

    val updatedUser = userService.updateUser(originalUser, newNickname, newPicture)

    assertNotNull(updatedUser)
    assertEquals(newNickname, updatedUser.nickname)
    assertEquals(newPicture, updatedUser.picture)
  }

  @Test
  fun `updateUser should not update when nickname and picture are null`() {
    val id = "test-id"
    val provider = AuthProvider.GOOGLE
    val originalUser = User(id, provider, "test@test.com", "oldNickname", "https://old.com", Role.USER)

    `when`(userRepository.save(originalUser)).thenAnswer { invocation ->
      invocation.getArgument<User>(0)
    }

    val updatedUser = userService.updateUser(originalUser, null, null)

    assertNotNull(updatedUser)
    assertEquals("oldNickname", updatedUser.nickname)
    assertEquals("https://old.com", updatedUser.picture)
  }

  @Test
  fun `updateUser should not update when nickname and picture are empty`() {
    val id = "test-id"
    val provider = AuthProvider.GOOGLE
    val originalUser = User(id, provider, "test@test.com", "oldNickname", "https://old.com", Role.USER)

    `when`(userRepository.save(originalUser)).thenAnswer { invocation ->
      invocation.getArgument<User>(0)
    }

    val updatedUser = userService.updateUser(originalUser, "", "")

    assertNotNull(updatedUser)
    assertEquals("oldNickname", updatedUser.nickname)
    assertEquals("https://old.com", updatedUser.picture)
  }
}
