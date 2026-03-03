package com.main.model

fun getMongoDbUri(): String {
    return System.getenv("MONGODB_URI")
        ?: throw IllegalStateException("MONGODB_URI environment variable is not set")
}

