package com.suspension.model

import com.main.model.createMongoClient
import com.main.model.getMongoDbUri
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.reactive.awaitFirstOrNull
import org.bson.Document
import org.bson.types.ObjectId
import java.util.Date

fun createSuspension(userId: String, suspendedUntil: Date, reason: String?, createdByUserId: String): Suspension = runBlocking {
    val uri = getMongoDbUri()
    val mongoClient = createMongoClient(uri)
    val collection = mongoClient.getDatabase("Users").getCollection("Suspensions")

    val now = Date()
    val id = ObjectId()
    val doc = Document()
        .append("_id", id)
        .append("userId", userId)
        .append("suspendedFrom", now)
        .append("suspendedUntil", suspendedUntil)
        .append("reason", reason)
        .append("createdByUserId", createdByUserId)
        .append("createdAt", now)

    collection.insertOne(doc).awaitFirstOrNull()
    mongoClient.close()

    Suspension(
        id = id.toString(),
        userId = userId,
        suspendedFrom = now,
        suspendedUntil = suspendedUntil,
        reason = reason,
        createdByUserId = createdByUserId,
        createdAt = now
    )
}
