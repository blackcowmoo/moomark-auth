package com.blackcowmoo.moomark.auth.util

import org.junit.jupiter.api.Assertions.assertArrayEquals
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class AesUtilTest {

  @Autowired
  private lateinit var aesUtil: AesUtil

  @Test
  fun `encrypt and decrypt with generated key`() {
    val key = aesUtil.generateNewKey()
    assertNotNull(key)

    val originalText = "Hello, World!"
    val encrypted = aesUtil.encrypt(originalText, key)
    assertNotNull(encrypted)

    val decrypted = aesUtil.decrypt(encrypted, key)
    assertEquals(originalText, decrypted)
  }

  @Test
  fun `encrypt and decrypt with another generated key`() {
    val key = aesUtil.generateNewKey()
    assertNotNull(key)

    val originalText = "Test encryption/decryption"
    val encrypted = aesUtil.encrypt(originalText, key)
    assertNotNull(encrypted)

    val decrypted = aesUtil.decrypt(encrypted, key)
    assertEquals(originalText, decrypted)
  }

  @Test
  fun `encrypt with one key and decrypt with another key should fail`() {
    val key1 = aesUtil.generateNewKey()
    val key2 = aesUtil.generateNewKey()

    assertNotNull(key1)
    assertNotNull(key2)

    val originalText = "Test with different keys"
    val encrypted = aesUtil.encrypt(originalText, key1)

    val decrypted = aesUtil.decrypt(encrypted, key2)
    assertNotEquals(originalText, decrypted)
  }

  @Test
  fun `generateNewKey returns different keys`() {
    val key1 = aesUtil.generateNewKey()
    val key2 = aesUtil.generateNewKey()

    assertNotNull(key1)
    assertNotNull(key2)
  }
}
