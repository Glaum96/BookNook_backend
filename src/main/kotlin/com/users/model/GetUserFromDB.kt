package com.users.model

import com.main.model.createMongoClient
import com.main.model.getMongoDbUri
import kotlinx.coroutines.reactive.awaitFirstOrNull
import kotlinx.coroutines.runBlocking
import org.bson.Document
import org.bson.types.ObjectId

fun getUserFromDB(userId: String): User? = runBlocking {

    val uri = getMongoDbUri()
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