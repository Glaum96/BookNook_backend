package com.bookings.model

import com.main.model.createMongoClient
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.reactive.awaitFirstOrNull
import org.bson.Document

fun postNewBooking(newBooking: Booking) = runBlocking {

    val uri = "mongodb+srv://takterrassen:Seilduksgata6B@takterrassen.jz3qs.mongodb.net/?retryWrites=true&w=majority&appName=Takterrassen"

    val mongoClient = createMongoClient(uri)

    val database = mongoClient.getDatabase("Bookings")
    val collection = database.getCollection("Bookings")
    val booking = mutableListOf<Booking>()

    runBlocking {


        val userDocument = Document()
            .append("from", newBooking.startTime)
            .append("to", newBooking.endTime)
            .append("bookerId", newBooking.userId)
            .append("responsibleName", newBooking.responsibleName)
            .append("responsibleNumber", newBooking.responsibleNumber)

        collection.insertOne(userDocument).awaitFirstOrNull()


    }


    mongoClient.close()
    return@runBlocking booking
}
