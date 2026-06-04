package com.suspension.model

import com.main.model.createMongoClient
import com.main.model.getMongoDbUri
import com.mongodb.client.model.Filters
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.reactive.awaitFirstOrNull
import java.util.Date

fun deleteActiveSuspension(userId: String): Boolean = runBlocking {
    val uri = getMongoDbUri()
    val mongoClient = createMongoClient(uri)
    val collection = mongoClient.getDatabase("Users").getCollection("Suspensions")

    val now = Date()
    val result = collection.deleteOne(
        Filters.and(
            Filters.eq("userId", userId),
            Filters.lte("suspendedFrom", now),
            Filters.gte("suspendedUntil", now)
        )
    ).awaitFirstOrNull()

    mongoClient.close()
    (result?.deletedCount ?: 0L) > 0L
}
