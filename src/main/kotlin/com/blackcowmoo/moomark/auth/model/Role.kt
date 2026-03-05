package com.blackcowmoo.moomark.auth.model

enum class Role(val key: String, val title: String) {
    ADMIN("ADMIN", "관리자"),
    USER("USER", "유저");
}