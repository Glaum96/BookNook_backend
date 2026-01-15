package com.bookings.model

import com.main.model.createMongoClient
import com.main.model.getMongoDbUri
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.reactive.awaitFirstOrNull
import org.bson.Document

fun postBookingToDB(newBooking: Booking) = runBlocking {

    val uri = getMongoDbUri()

    val mongoClient = createMongoClient(uri)

    val database = mongoClient.getDatabase("Bookings")
    val collection = database.getCollection("Bookings")
    val booking = mutableListOf<Booking>()

    runBlocking {
        val userDocument = Document()
            .append("from", newBooking.startTime)
            .append("to", newBooking.endTime)
            .append("userId", newBooking.userId)
            .append("responsibleName", newBooking.responsibleName)
            .append("responsibleNumber", newBooking.responsibleNumber)

        collection.insertOne(userDocument).awaitFirstOrNull()
    }


    mongoClient.close()
    return@runBlocking booking
}
