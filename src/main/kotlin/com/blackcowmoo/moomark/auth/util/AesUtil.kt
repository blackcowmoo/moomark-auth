package com.blackcowmoo.moomark.auth.util

import lombok.extern.slf4j.Slf4j
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.security.NoSuchAlgorithmException
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey

@Component
@Slf4j
class AesUtil {
  companion object {
    private val log = LoggerFactory.getLogger(AesUtil::class.java)
  }
  private val cipher = "AES"
  private val secureRandom = SecureRandom()

  @Throws(Exception::class)
  fun encrypt(body: String, key: SecretKey): ByteArray {
    val cipher = Cipher.getInstance("AES")
    cipher.init(Cipher.ENCRYPT_MODE, key)
    val encrypted = cipher.doFinal(body.toByteArray())
    return encrypted
  }

  fun decrypt(body: ByteArray, key: SecretKey): String {
    val cipher = Cipher.getInstance("AES")
    cipher.init(Cipher.DECRYPT_MODE, key)
    val decrypted = cipher.doFinal(body)
    return String(decrypted, Charsets.UTF_8)
  }

  fun generateNewKey(): SecretKey? {
    return getRandomKey(cipher, 128)
  }

  private fun getRandomKey(cipher: String, keySize: Int): SecretKey? {
    return try {
      val generator = KeyGenerator.getInstance(cipher)
      val random = SecureRandom.getInstance("SHA1PRNG")
      random.setSeed(getRandomBytes(keySize))
      generator.init(128, random)
      generator.generateKey()
    } catch (e: NoSuchAlgorithmException) {
      log.error(e.message, e)
      null
    }
  }

  private fun getRandomBytes(keySize: Int): ByteArray {
    val secureRandomKeyBytes = ByteArray(keySize / 8)
    secureRandom.nextBytes(secureRandomKeyBytes)
    return secureRandomKeyBytes
  }
}
