package com.blackcowmoo.moomark.auth

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication

@SpringBootApplication
open class AuthApplication

fun main(args: Array<String>) {
  SpringApplication.run(arrayOf(AuthApplication::class.java), args)
}
