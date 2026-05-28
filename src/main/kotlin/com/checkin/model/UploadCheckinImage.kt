package com.checkin.model

import com.bookings.model.getAllBookingsFromDB
import com.main.model.getMongoDbUri
import com.mongodb.ConnectionString
import com.mongodb.MongoClientSettings
import com.mongodb.client.MongoClients
import com.mongodb.client.gridfs.GridFSBuckets
import com.mongodb.client.gridfs.model.GridFSUploadOptions
import org.bson.Document
import java.io.InputStream
import java.util.Date

class CheckinTimeWindowException(message: String) : Exception(message)

fun uploadCheckinImage(
    bookingId: String,
    userId: String,
    type: String,
    filename: String,
    contentType: String,
    inputStream: InputStream
): String {
    val booking = getAllBookingsFromDB().find { it.id == bookingId }
        ?: throw IllegalArgumentException("Booking not found: $bookingId")

    val now = Date()
    val fiveMinutes = 5 * 60 * 1000L

    when (type) {
        "CHECK_IN" -> {
            val deadline = Date(booking.startTime.time + fiveMinutes)
            if (now.after(deadline)) {
                throw CheckinTimeWindowException("Check-in er kun mulig frem til 5 minutter etter bookingen startet.")
            }
        }
        "CHECK_OUT" -> {
            val earliest = Date(booking.endTime.time - fiveMinutes)
            if (now.before(earliest)) {
                throw CheckinTimeWindowException("Check-out er kun mulig fra 5 minutter før bookingen slutter.")
            }
        }
        else -> throw IllegalArgumentException("Ugyldig type: $type")
    }

    val uri = getMongoDbUri()
    val settings = MongoClientSettings.builder()
        .applyConnectionString(ConnectionString(uri))
        .build()
    val mongoClient = MongoClients.create(settings)

    val database = mongoClient.getDatabase("checkin_images")
    val bucket = GridFSBuckets.create(database)

    val metadata = Document()
        .append("bookingId", bookingId)
        .append("userId", userId)
        .append("type", type)
        .append("uploadedAt", now)
        .append("contentType", contentType)

    val options = GridFSUploadOptions().chunkSizeBytes(255 * 1024).metadata(metadata)
    val fileId = bucket.uploadFromStream(filename, inputStream, options)

    mongoClient.close()

    pruneOldImages()

    return fileId.toString()
}
