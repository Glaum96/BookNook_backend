package com.bookings.model

import com.main.model.createMongoClient
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.reactive.awaitFirstOrNull
import org.bson.Document
import org.bson.types.ObjectId

fun deleteMyBooking(bookingId: String) = runBlocking {

    val uri = "mongodb+srv://takterrassen:Seilduksgata6B@takterrassen.jz3qs.mongodb.net/?retryWrites=true&w=majority&appName=Takterrassen"

    val mongoClient = createMongoClient(uri)
    val database = mongoClient.getDatabase("Bookings")
    val collection = database.getCollection("Bookings")

    var returnText = ""
    runBlocking {
        println(bookingId)
        val deleteResult = collection.deleteOne(Document("_id", ObjectId(bookingId))).awaitFirstOrNull()
        returnText = if (deleteResult?.deletedCount == 1L) {
            "Booking with ID $bookingId deleted successfully."
        } else {
            "Booking with ID $bookingId not found."
        }
    }

    mongoClient.close()
    return@runBlocking returnText
}
