package com.blackcowmoo.moomark.auth.model.entity

import com.blackcowmoo.moomark.auth.model.AuthProvider
import com.blackcowmoo.moomark.auth.model.Role
import java.io.Serializable
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.persistence.Id
import javax.persistence.IdClass

@Entity
@IdClass(UserId::class)
class User : Serializable {
    @Id
    @Enumerated(EnumType.STRING)
    var authProvider: AuthProvider? = null

    @Id
    var id: String? = null

    @Column(nullable = false)
    var email: String? = null

    @Column
    var nickname: String? = null

    @Column
    var picture: String? = null

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var role: Role? = null

    constructor() {}

    constructor(id: String?, authProvider: AuthProvider?, email: String?, nickname: String?, picture: String?, role: Role?) {
        this.id = id
        this.email = email
        this.nickname = nickname
        this.picture = picture
        this.role = role
        this.authProvider = authProvider
    }

    fun updateNickname(nickname: String): User {
        this.nickname = nickname
        return this
    }

    fun updatePicture(picture: String): User {
        this.picture = picture
        return this
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || other !is User) return false
        return id == other.id && authProvider == other.authProvider
    }

    override fun hashCode(): Int {
        return java.util.Objects.hash(id, authProvider)
    }
}