package com.bookings.model

import com.main.model.createMongoClient
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.reactive.awaitFirstOrNull
import org.bson.Document
import com.mongodb.client.model.Filters

fun isBookingAvailable(newBooking: Booking): Boolean = runBlocking {
    val uri = "mongodb+srv://takterrassen:Seilduksgata6B@takterrassen.jz3qs.mongodb.net/?retryWrites=true&w=majority&appName=Takterrassen"
    val mongoClient = createMongoClient(uri)
    val database = mongoClient.getDatabase("Bookings")
    val collection = database.getCollection("Bookings")

    val overlappingBooking = collection.find(
        Filters.and(
            Filters.lt("from", newBooking.endTime),  // Startet før den nye bookingen slutter
            Filters.gt("to", newBooking.startTime)   // Sluttet etter den nye bookingen startet
        )
    ).awaitFirstOrNull()

    mongoClient.close()
    return@runBlocking overlappingBooking == null
}

// Legger til en booking hvis tidsrommet er ledig
fun postBookingToDB(newBooking: Booking): Boolean = runBlocking {
    if (!isBookingAvailable(newBooking)) {
        return@runBlocking false // Returner false hvis tidsrommet er opptatt
    }

    val uri = "mongodb+srv://takterrassen:Seilduksgata6B@takterrassen.jz3qs.mongodb.net/?retryWrites=true&w=majority&appName=Takterrassen"
    val mongoClient = createMongoClient(uri)

    val database = mongoClient.getDatabase("Bookings")
    val collection = database.getCollection("Bookings")

    val bookingDocument = Document()
        .append("from", newBooking.startTime)
        .append("to", newBooking.endTime)
        .append("userId", newBooking.userId)
        .append("responsibleName", newBooking.responsibleName)
        .append("responsibleNumber", newBooking.responsibleNumber)

    collection.insertOne(bookingDocument).awaitFirstOrNull()

    mongoClient.close()
    return@runBlocking true
}

