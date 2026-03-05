package com.blackcowmoo.moomark.auth.service

import com.blackcowmoo.moomark.auth.model.AuthProvider
import com.blackcowmoo.moomark.auth.model.dto.Passport
import com.blackcowmoo.moomark.auth.model.dto.PassportResponse
import com.blackcowmoo.moomark.auth.model.entity.User
import com.blackcowmoo.moomark.auth.util.AesUtil
import com.blackcowmoo.moomark.auth.util.RsaUtil
import com.fasterxml.jackson.databind.ObjectMapper
import lombok.extern.slf4j.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.sql.Timestamp
import java.time.LocalDateTime
import java.util.Base64
import javax.annotation.PostConstruct
import javax.crypto.SecretKey
import javax.crypto.spec.SecretKeySpec
import javax.xml.bind.DatatypeConverter

@Slf4j
@Service
class PassportService {

    @Value("\${passport.public-key}")
    private lateinit var publicKeyString: String

    @Value("\${passport.private-key}")
    private lateinit var privateKeyString: String

    private var passportExpireSeconds: Long = 120L

    @Autowired
    private lateinit var mapper: ObjectMapper

    private val encoder: Base64.Encoder = Base64.getEncoder()
    private val decoder: Base64.Decoder = Base64.getDecoder()

    private lateinit var rsaUtil: RsaUtil

    @Autowired
    private lateinit var aesUtil: AesUtil

    @PostConstruct
    fun buildRsaKeys() {
        rsaUtil = RsaUtil(publicKeyString, privateKeyString)
    }

    fun getPublicKeyString(): String {
        return publicKeyString
    }

    fun parsePassport(passport: String, passportKey: String): User? {
        return try {
            val passportResult = decryptPassport(passportKey)
            if (passportResult.exp.after(Timestamp.valueOf(LocalDateTime.now()))) {
                val hash = passportResult.hash
                val key = SecretKeySpec(decoder.decode(passportResult.key), "AES")
                val userBody = aesUtil.decrypt(decoder.decode(passport), key)
                if (getHash(userBody) == hash) {
                    mapper.readValue(decoder.decode(userBody), User::class.java)
                } else {
                    null
                }
            } else {
                null
            }
        } catch (e: Exception) {
            log.error(e.message, e)
            null
        }
    }

    fun generatePassport(user: User): PassportResponse? {
        return try {
            val key = getAesKey(user.authProvider, user.id)
            val userBody = encoder.encodeToString(mapper.writeValueAsString(user).toByteArray())

            val passport = Passport()
            passport.exp = Timestamp.valueOf(LocalDateTime.now().plusSeconds(passportExpireSeconds))
            passport.key = encoder.encodeToString(key.encoded)
            passport.hash = getHash(userBody)

            val response = PassportResponse()
            response.passport = encoder.encodeToString(aesUtil.encrypt(userBody, key))
            response.key = encryptPassport(passport)
            response
        } catch (e: Exception) {
            log.error(e.message, e)
            null
        }
    }

    private fun getHash(user: String): String {
        val md = MessageDigest.getInstance("MD5")
        val digest = md.digest(user.toByteArray(StandardCharsets.UTF_8))
        return DatatypeConverter.printHexBinary(digest)
    }

    private fun encryptPassport(passport: Passport): String {
        return encoder.encodeToString(rsaUtil.encryptByPrivateKey(mapper.writeValueAsString(passport)))
    }

    private fun decryptPassport(passport: String): Passport {
        return mapper.readValue(rsaUtil.decryptByPublicKey(decoder.decode(passport)), Passport::class.java)
    }

    private fun getAesKey(provider: AuthProvider, id: String): SecretKey {
        return aesUtil.generateNewKey()
    }
}