package com.users.model

import com.main.model.createMongoClient
import com.main.model.getMongoDbUri
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.runBlocking

fun getUsersFromDb() = runBlocking {

    val uri = getMongoDbUri()

    val mongoClient = createMongoClient(uri)

    val database = mongoClient.getDatabase("Users")
    val collection = database.getCollection("Users")
    val users = mutableListOf<User>()

    runBlocking {
        val docs = collection.find().asFlow().toList()
        for (doc in docs) {
            users.add(
                User(
                    id = doc.getObjectId("_id").toString(),
                    name = doc.getString("name"),
                    email = doc.getString("email"),
                    phoneNumber = doc.getString("phoneNumber") ?: "",
                    apartmentNumber = doc.getString("apartmentNumber") ?: "",
                    isAdmin = doc.getBoolean("isAdmin") ?: false
                )
            )
        }
    }

    mongoClient.close()
    return@runBlocking users
}
