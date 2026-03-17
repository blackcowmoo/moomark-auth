package com.blackcowmoo.moomark.auth.util

import lombok.extern.slf4j.Slf4j
import org.slf4j.LoggerFactory
import java.security.KeyFactory
import java.security.PrivateKey
import java.security.PublicKey
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.X509EncodedKeySpec
import java.util.Base64
import javax.crypto.Cipher

@Slf4j
open class RsaUtil(
  publicKeyBase64String: String,
  privateKeyBase64String: String
) {
  companion object {
    private val log = LoggerFactory.getLogger(RsaUtil::class.java)
  }

  private val keyFactory: KeyFactory
  private val cipher: Cipher
  private val publicKey: PublicKey
  private val privateKey: PrivateKey

  init {
    keyFactory = KeyFactory.getInstance("RSA")
    cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding")
    publicKey = buildPublicKey(publicKeyBase64String)
    privateKey = buildPrivateKey(privateKeyBase64String)
  }

  @Throws(Exception::class)
  open fun buildPublicKey(publicKeyBase64String: String): PublicKey {
    val ukeySpec = X509EncodedKeySpec(Base64.getDecoder().decode(publicKeyBase64String))
    return keyFactory.generatePublic(ukeySpec)
  }

  @Throws(Exception::class)
  open fun buildPrivateKey(privateKeyBase64String: String): PrivateKey {
    val rkeySpec = PKCS8EncodedKeySpec(Base64.getDecoder().decode(privateKeyBase64String))
    return keyFactory.generatePrivate(rkeySpec)
  }

  open fun encryptByPublicKey(data: String): ByteArray? {
    return try {
      cipher.init(Cipher.ENCRYPT_MODE, publicKey)
      cipher.doFinal(data.toByteArray())
    } catch (e: Exception) {
      log.error("encryptByPublicKey: ", e)
      null
    }
  }

  open fun decryptByPublicKey(data: ByteArray): String? {
    return try {
      cipher.init(Cipher.DECRYPT_MODE, publicKey)
      String(cipher.doFinal(data), Charsets.UTF_8)
    } catch (e: Exception) {
      log.error("decryptByPublicKey: ", e)
      null
    }
  }

  open fun encryptByPrivateKey(data: String): ByteArray? {
    return encryptByPrivateKey(data.toByteArray())
  }

  open fun encryptByPrivateKey(data: ByteArray): ByteArray? {
    return try {
      cipher.init(Cipher.ENCRYPT_MODE, privateKey)
      cipher.doFinal(data)
    } catch (e: Exception) {
      log.error("encryptByPrivateKey: ", e)
      null
    }
  }

  open fun decryptByPrivateKey(data: ByteArray): String? {
    return try {
      cipher.init(Cipher.DECRYPT_MODE, privateKey)
      String(cipher.doFinal(data), Charsets.UTF_8)
    } catch (e: Exception) {
      log.error("decryptByPrivateKey: ", e)
      null
    }
  }
}
