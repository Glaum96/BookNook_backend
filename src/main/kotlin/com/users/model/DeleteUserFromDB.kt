package com.users.model

import com.main.model.createMongoClient
import kotlinx.coroutines.reactive.awaitFirstOrNull
import kotlinx.coroutines.runBlocking
import org.bson.Document
import org.bson.types.ObjectId

fun deleteUserFromDB(userId: String) = runBlocking {

    val uri = "mongodb+srv://booknook:***REMOVED***@booknookcluster.eicfcms.mongodb.net/?appName=BookNookCluster"

    val mongoClient = createMongoClient(uri)
    val database = mongoClient.getDatabase("Users")
    val collection = database.getCollection("Users")

    var returnText = ""
    runBlocking {
        val deleteUser = collection.deleteOne(Document("_id", ObjectId(userId))).awaitFirstOrNull()
        returnText = if (deleteUser?.deletedCount == 1L) {
            "User with ID $userId deleted successfully."
        } else {
            "User with ID $userId not found."
        }
    }

    mongoClient.close()
    return@runBlocking returnText
}