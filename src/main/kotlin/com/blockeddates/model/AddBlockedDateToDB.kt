package com.blockeddates.model

import com.main.model.createMongoClient
import com.main.model.getMongoDbUri
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.runBlocking
import org.bson.Document
import java.util.UUID

fun addBlockedDateToDB(date: String, label: String?): BlockedDate = runBlocking {
    val uri = getMongoDbUri()
    val mongoClient = createMongoClient(uri)
    val collection = mongoClient.getDatabase("Bookings").getCollection("BlockedDates")

    val id = UUID.randomUUID().toString()
    val doc = Document()
        .append("id", id)
        .append("date", date)
    if (label != null) doc.append("label", label)

    collection.insertOne(doc).asFlow().toList()
    mongoClient.close()
    BlockedDate(id = id, date = date, label = label)
}
