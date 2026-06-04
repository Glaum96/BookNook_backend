package com.blockeddates.model

import com.main.model.createMongoClient
import com.main.model.getMongoDbUri
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.runBlocking
import org.bson.Document

fun deleteBlockedDateFromDB(id: String): Boolean = runBlocking {
    val uri = getMongoDbUri()
    val mongoClient = createMongoClient(uri)
    val collection = mongoClient.getDatabase("Bookings").getCollection("BlockedDates")

    val result = collection.deleteOne(Document("id", id)).asFlow().toList()
    mongoClient.close()
    result.firstOrNull()?.deletedCount?.let { it > 0 } ?: false
}
