package com.suspension.model

import com.main.model.createMongoClient
import com.main.model.getMongoDbUri
import com.mongodb.client.model.Filters
import com.mongodb.client.model.Updates
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.reactive.awaitFirstOrNull
import java.util.Date

fun updateActiveSuspension(userId: String, newUntil: Date, reason: String?): Boolean = runBlocking {
    val uri = getMongoDbUri()
    val mongoClient = createMongoClient(uri)
    val collection = mongoClient.getDatabase("Users").getCollection("Suspensions")

    val now = Date()
    val updates = mutableListOf(Updates.set("suspendedUntil", newUntil))
    if (reason != null) updates.add(Updates.set("reason", reason))

    val result = collection.updateOne(
        Filters.and(
            Filters.eq("userId", userId),
            Filters.lte("suspendedFrom", now),
            Filters.gte("suspendedUntil", now)
        ),
        Updates.combine(updates)
    ).awaitFirstOrNull()

    mongoClient.close()
    (result?.modifiedCount ?: 0L) > 0L
}
