package com.login.model

import jakarta.persistence.Entity
import jakarta.persistence.Id

@Entity
data class LoginCredentials(
    @Id
    val id: String,
    val username: String,
    val password: String
)