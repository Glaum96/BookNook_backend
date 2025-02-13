package com.login.model

import com.main.model.createMongoClient
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.reactive.awaitFirstOrNull
import org.bson.Document

fun getUserByUsername(username: String): LoginCredentials? = runBlocking {
    val uri = "mongodb+srv://takterrassen:Seilduksgata6B@takterrassen.jz3qs.mongodb.net/?retryWrites=true&w=majority&appName=Takterrassen"
    val mongoClient = createMongoClient(uri)
    val database = mongoClient.getDatabase("Users")
    val collection = database.getCollection("LoginCredentials")


    val userDoc = collection.find(Document("username", username)).awaitFirstOrNull()
    println("userDoc from DB: $userDoc")
    mongoClient.close()

    return@runBlocking userDoc?.let {
        LoginCredentials(
            id = it.getObjectId("_id").toString(),
            username = it.getString("username"),
            password = it.getString("password")
        )
    }
}