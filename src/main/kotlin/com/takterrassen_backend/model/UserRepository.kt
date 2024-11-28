package com.takterrassen_backend.model

import org.springframework.data.mongodb.repository.MongoRepository


interface UserRepository : MongoRepository<UserMongo?, String?> {
    fun findByAge(age: Int): List<UserMongo?>?
}