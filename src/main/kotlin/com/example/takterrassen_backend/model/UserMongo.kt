package com.example.takterrassen_backend.model

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "Users")
class UserMongo {
    @Id
    private val _id: String? = null
    private val name: String? = null
    private val email: String? = null
    private val age = 0 // Getters and setters
    private val hobbies: List<String>? = null
}