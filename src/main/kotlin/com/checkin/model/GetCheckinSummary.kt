package com.checkin.model

import com.main.model.getMongoDbUri
import com.mongodb.ConnectionString
import com.mongodb.MongoClientSettings
import com.mongodb.client.MongoClients
import org.bson.Document

data class BookingCheckinSummary(
    val bookingId: String,
    val hasCheckin: Boolean,
    val hasCheckout: Boolean
)

fun getCheckinSummaryForRecentBookings(): List<BookingCheckinSummary> {
    val uri = getMongoDbUri()
    val settings = MongoClientSettings.builder()
        .applyConnectionString(ConnectionString(uri))
        .build()
    val mongoClient = MongoClients.create(settings)

    val allBookings = mongoClient.getDatabase("Bookings")
        .getCollection("Bookings")
        .find()
        .toList()
        .sortedByDescending { it.getDate("from") }

    val recentIds = allBookings.take(10).map { it.getObjectId("_id").toString() }.toSet()

    val hasCheckin = mutableSetOf<String>()
    val hasCheckout = mutableSetOf<String>()

    mongoClient.getDatabase("checkin_images")
        .getCollection("fs.files")
        .find()
        .forEach { doc ->
            val meta = doc.get("metadata", Document::class.java) ?: return@forEach
            val bookingId = meta.getString("bookingId") ?: return@forEach
            if (bookingId !in recentIds) return@forEach
            when (meta.getString("type")) {
                "CHECK_IN" -> hasCheckin.add(bookingId)
                "CHECK_OUT" -> hasCheckout.add(bookingId)
            }
        }

    mongoClient.close()

    return recentIds.map { id ->
        BookingCheckinSummary(
            bookingId = id,
            hasCheckin = id in hasCheckin,
            hasCheckout = id in hasCheckout
        )
    }
}
