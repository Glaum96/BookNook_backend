package com.checkin.model

import com.main.model.getMongoDbUri
import com.mongodb.ConnectionString
import com.mongodb.MongoClientSettings
import com.mongodb.client.MongoClients
import com.mongodb.client.model.Filters
import org.bson.Document

fun pruneOldImages() {
    val uri = getMongoDbUri()
    val settings = MongoClientSettings.builder()
        .applyConnectionString(ConnectionString(uri))
        .build()
    val mongoClient = MongoClients.create(settings)

    val bookingsDb = mongoClient.getDatabase("Bookings")
    val bookingsCollection = bookingsDb.getCollection("Bookings")

    val allBookings = bookingsCollection.find().toList()
    val sortedBookings = allBookings.sortedByDescending { it.getDate("from") }
    val keepIds = sortedBookings.take(10).map { it.getObjectId("_id").toString() }.toSet()

    val imagesDb = mongoClient.getDatabase("checkin_images")
    val filesCollection = imagesDb.getCollection("fs.files")
    val chunksCollection = imagesDb.getCollection("fs.chunks")

    val filesToDelete = filesCollection.find().filter { file ->
        val bookingId = file.get("metadata", Document::class.java)?.getString("bookingId")
        bookingId != null && bookingId !in keepIds
    }

    for (file in filesToDelete) {
        val fileId = file.getObjectId("_id")
        filesCollection.deleteOne(Filters.eq("_id", fileId))
        chunksCollection.deleteMany(Filters.eq("files_id", fileId))
    }

    mongoClient.close()
}
