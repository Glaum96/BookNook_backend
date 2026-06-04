package com.users.model

import com.main.model.createMongoClient
import com.main.model.getMongoDbUri
import kotlinx.coroutines.reactive.awaitFirstOrNull
import kotlinx.coroutines.runBlocking
import org.bson.Document

fun deleteUserFromDB(userId: String) = runBlocking {

    val uri = getMongoDbUri()

    val mongoClient = createMongoClient(uri)
    val database = mongoClient.getDatabase("Users")
    val collection = database.getCollection("Users")

    var returnText = ""
    runBlocking {
        val deleteUser = collection.deleteOne(Document("loginObjectId", userId)).awaitFirstOrNull()
        returnText = if (deleteUser?.deletedCount == 1L) {
            "User with ID $userId deleted successfully."
        } else {
            "User with ID $userId not found."
        }
    }

    mongoClient.close()
    return@runBlocking returnText
}