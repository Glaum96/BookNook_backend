package com.bookings.model

import com.main.model.createMongoClient
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.runBlocking

fun main() {
    getBookings()
}


fun getBookings() = runBlocking {

    val uri = "mongodb+srv://takterrassen:Seilduksgata6B@takterrassen.jz3qs.mongodb.net/?retryWrites=true&w=majority&appName=Takterrassen"

    val mongoClient = createMongoClient(uri)

    val database = mongoClient.getDatabase("Bookings")
    val collection = database.getCollection("Bookings")
    val bookings = mutableListOf<Booking>()

    runBlocking {
        val docs = collection.find().asFlow().toList()
        for (doc in docs) {
            bookings.add(
                Booking(
                    id = doc.getObjectId("_id").toString(),
                    startTime = doc.getDate("from"),
                    endTime = doc.getDate("to"),
                    userId = doc.getString("userId"),
                    responsibleNumber = doc.getString("responsibleNumber"),
                    responsibleName = doc.getString("responsibleName")
                )
            )
        }
    }

    mongoClient.close()
    return@runBlocking bookings
}
