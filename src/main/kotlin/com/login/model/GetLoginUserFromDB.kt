package com.login.model

import com.main.model.createMongoClient
import com.main.model.getMongoDbUri
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.reactive.awaitFirstOrNull
import org.bson.Document

fun getUserByUsername(username: String): LoginCredentials? = runBlocking {
    val uri = getMongoDbUri()
    val mongoClient = createMongoClient(uri)
    val database = mongoClient.getDatabase("Users")
    val collection = database.getCollection("LoginCredentials")


    val userDoc = collection.find(Document("username", username)).awaitFirstOrNull()
    println("userDoc from DB: $userDoc")
    mongoClient.close()

    return@runBlocking userDoc?.let {
        LoginCredentials(
            id = it.getString("id").toString(),
            username = it.getString("username"),
            password = it.getString("password")
        )
    }
}