package com.blackcowmoo.moomark.auth.model.entity

import com.blackcowmoo.moomark.auth.model.AuthProvider
import java.io.Serializable

class UserId : Serializable {
    var authProvider: AuthProvider? = null
    var id: String? = null

    constructor()

    constructor(authProvider: AuthProvider, id: String) {
        this.authProvider = authProvider
        this.id = id
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || other !is UserId) return false
        return id == other.id && authProvider == other.authProvider
    }

    override fun hashCode(): Int {
        return java.util.Objects.hash(id, authProvider)
    }
}