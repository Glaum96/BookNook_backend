package com.suspension.model

import com.main.model.createMongoClient
import com.main.model.getMongoDbUri
import com.mongodb.client.model.Filters
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.reactive.awaitFirstOrNull
import java.util.Date

fun getActiveSuspensionForUser(userId: String): Suspension? = runBlocking {
    val uri = getMongoDbUri()
    val mongoClient = createMongoClient(uri)
    val collection = mongoClient.getDatabase("Users").getCollection("Suspensions")

    val now = Date()
    val doc = collection.find(
        Filters.and(
            Filters.eq("userId", userId),
            Filters.lte("suspendedFrom", now),
            Filters.gte("suspendedUntil", now)
        )
    ).awaitFirstOrNull()

    mongoClient.close()

    doc?.let {
        Suspension(
            id = it.getObjectId("_id").toString(),
            userId = it.getString("userId"),
            suspendedFrom = it.getDate("suspendedFrom"),
            suspendedUntil = it.getDate("suspendedUntil"),
            reason = it.getString("reason"),
            createdByUserId = it.getString("createdByUserId"),
            createdAt = it.getDate("createdAt")
        )
    }
}

fun getAllActiveSuspensions(): List<Suspension> = runBlocking {
    val uri = getMongoDbUri()
    val mongoClient = createMongoClient(uri)
    val collection = mongoClient.getDatabase("Users").getCollection("Suspensions")

    val now = Date()
    val docs = collection.find(
        Filters.and(
            Filters.lte("suspendedFrom", now),
            Filters.gte("suspendedUntil", now)
        )
    ).asFlow().toList()

    mongoClient.close()

    docs.map {
        Suspension(
            id = it.getObjectId("_id").toString(),
            userId = it.getString("userId"),
            suspendedFrom = it.getDate("suspendedFrom"),
            suspendedUntil = it.getDate("suspendedUntil"),
            reason = it.getString("reason"),
            createdByUserId = it.getString("createdByUserId"),
            createdAt = it.getDate("createdAt")
        )
    }
}
