package com.blockeddates.model

import com.main.model.createMongoClient
import com.main.model.getMongoDbUri
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.runBlocking
import org.bson.Document

private val defaultBlockedDates = listOf(
    Pair("2026-05-17", "17. mai")
)

fun getBlockedDatesFromDB(): List<BlockedDate> = runBlocking {
    val uri = getMongoDbUri()
    val mongoClient = createMongoClient(uri)
    val collection = mongoClient.getDatabase("Bookings").getCollection("BlockedDates")

    val docs = collection.find().asFlow().toList()

    if (docs.isEmpty()) {
        val seedDocs = defaultBlockedDates.map { (date, label) ->
            Document()
                .append("id", java.util.UUID.randomUUID().toString())
                .append("date", date)
                .append("label", label)
        }
        seedDocs.forEach { doc -> collection.insertOne(doc).asFlow().toList() }
        mongoClient.close()
        return@runBlocking defaultBlockedDates.map { (date, label) ->
            BlockedDate(id = java.util.UUID.randomUUID().toString(), date = date, label = label)
        }
    }

    val result = docs.map { doc ->
        BlockedDate(
            id = doc.getString("id") ?: doc.getObjectId("_id").toString(),
            date = doc.getString("date") ?: "",
            label = doc.getString("label")
        )
    }
    mongoClient.close()
    result
}
