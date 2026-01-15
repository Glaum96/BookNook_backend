package com.users.model

import com.main.model.createMongoClient
import com.main.model.getMongoDbUri
import kotlinx.coroutines.reactive.awaitFirstOrNull
import kotlinx.coroutines.runBlocking
import org.bson.Document
import org.bson.types.ObjectId

fun putUser(userId: String, updatedUser: User): Boolean = runBlocking {

    val uri = getMongoDbUri()

    val mongoClient = createMongoClient(uri)

    val database = mongoClient.getDatabase("Users")
    val collection = database.getCollection("Users")

    val updateResult = collection.updateOne(
        Document("_id", ObjectId(userId)),
        Document("\$set", Document()
            .append("name", updatedUser.name)
            .append("email", updatedUser.email)
            .append("phoneNumber", updatedUser.phoneNumber)
            .append("apartmentNumber", updatedUser.apartmentNumber)
        )
    ).awaitFirstOrNull()

    mongoClient.close()
    return@runBlocking updateResult?.modifiedCount == 1L
}