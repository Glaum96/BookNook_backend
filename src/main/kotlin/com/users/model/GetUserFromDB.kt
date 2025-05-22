package com.users.model

import com.main.model.createMongoClient
import kotlinx.coroutines.reactive.awaitFirstOrNull
import kotlinx.coroutines.runBlocking
import org.bson.Document
import org.bson.types.ObjectId

fun getUserFromDB(userId: String): User? = runBlocking {

    val uri = "mongodb+srv://takterrassen:Seilduksgata6B@takterrassen.jz3qs.mongodb.net/?retryWrites=true&w=majority&appName=Takterrassen"

    val mongoClient = createMongoClient(uri)

    val database = mongoClient.getDatabase("Users")
    val collection = database.getCollection("Users")

    val user = collection.find(Document("loginObjectId", userId)).awaitFirstOrNull()

    mongoClient.close()
    return@runBlocking user?.let {
        User(
            id = it.getString("loginObjectId"),
            name = it.getString("name"),
            email = it.getString("email"),
            phoneNumber = it.getString("phoneNumber"),
            apartmentNumber = it.getString("apartmentNumber"),
            isAdmin = it.getBoolean("isAdmin") ?: false
        )
    }
}