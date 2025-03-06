package com.users.model

data class User(
    val id: String,
    val name: String,
    val email: String,
    val phoneNumber: String,
    val apartmentNumber: String,
    val isAdmin: Boolean
)