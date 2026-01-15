package com.bookings.model

import com.main.model.createMongoClient
import com.mongodb.client.model.Filters
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.runBlocking
import org.bson.conversions.Bson

val uri = "mongodb+srv://booknook:bE5uEVvQYfturR2V@booknookcluster.eicfcms.mongodb.net/?appName=BookNookCluster"

fun getAllBookingsFromDB() = runBlocking {

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

fun getUserBookingsFromDB(userId: String, includePastBookings: Boolean) = runBlocking {
    val mongoClient = createMongoClient(uri)
    val database = mongoClient.getDatabase("Bookings")
    val collection = database.getCollection("Bookings")
    val bookings = mutableListOf<Booking>()

    runBlocking {
        val comingUserBookings = collection.find(getFilter(userId, includePastBookings)).asFlow().toList()
        for (booking in comingUserBookings) {
            bookings.add(
                Booking(
                    id = booking.getObjectId("_id").toString(),
                    startTime = booking.getDate("from"),
                    endTime = booking.getDate("to"),
                    userId = booking.getString("userId"),
                    responsibleNumber = booking.getString("responsibleNumber"),
                    responsibleName = booking.getString("responsibleName")
                )
            )
        }
    }

    bookings.sortByDescending {  it.startTime }

    mongoClient.close()
    return@runBlocking bookings
}

private fun getFilter(userId: String, includePastBookings: Boolean): Bson {
    val userFilter = Filters.eq("userId", userId)

    if (includePastBookings) {
        return userFilter
    } else {
        val timeFilter = Filters.gte("to", java.util.Date())
        return Filters.and(
            userFilter, timeFilter
        )
    }

}
