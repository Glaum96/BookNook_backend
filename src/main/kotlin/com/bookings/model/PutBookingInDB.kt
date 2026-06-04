package com.bookings.model

import com.main.model.createMongoClient
import com.main.model.getMongoDbUri
import kotlinx.coroutines.reactive.awaitFirstOrNull
import kotlinx.coroutines.runBlocking
import org.bson.Document
import org.bson.types.ObjectId

fun getBookingFromDB(bookingId: String): Booking? = runBlocking {
    val uri = getMongoDbUri()
    val mongoClient = createMongoClient(uri)
    val database = mongoClient.getDatabase("Bookings")
    val collection = database.getCollection("Bookings")

    val doc = collection.find(Document("_id", ObjectId(bookingId))).awaitFirstOrNull()

    mongoClient.close()
    doc?.let {
        Booking(
            id = it.getObjectId("_id").toString(),
            startTime = it.getDate("from"),
            endTime = it.getDate("to"),
            userId = it.getString("userId"),
            responsibleName = it.getString("responsibleName"),
            responsibleNumber = it.getString("responsibleNumber")
        )
    }
}

fun putBookingInDB(bookingId: String, updatedBooking: Booking): Boolean = runBlocking {
    val uri = getMongoDbUri()
    val mongoClient = createMongoClient(uri)
    val database = mongoClient.getDatabase("Bookings")
    val collection = database.getCollection("Bookings")

    val updateResult = collection.updateOne(
        Document("_id", ObjectId(bookingId)),
        Document("\$set", Document()
            .append("from", updatedBooking.startTime)
            .append("to", updatedBooking.endTime)
            .append("userId", updatedBooking.userId)
            .append("responsibleName", updatedBooking.responsibleName)
            .append("responsibleNumber", updatedBooking.responsibleNumber)
        )
    ).awaitFirstOrNull()

    mongoClient.close()
    updateResult?.modifiedCount == 1L
}
