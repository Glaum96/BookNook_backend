package com.checkin.model

import com.main.model.getMongoDbUri
import com.mongodb.ConnectionString
import com.mongodb.MongoClientSettings
import com.mongodb.client.MongoClients
import com.mongodb.client.model.Filters
import org.bson.Document

fun getCheckinImages(bookingId: String): List<CheckinImage> {
    val uri = getMongoDbUri()
    val settings = MongoClientSettings.builder()
        .applyConnectionString(ConnectionString(uri))
        .build()
    val mongoClient = MongoClients.create(settings)

    val database = mongoClient.getDatabase("checkin_images")
    val filesCollection = database.getCollection("fs.files")

    val images = mutableListOf<CheckinImage>()
    val docs = filesCollection.find(Filters.eq("metadata.bookingId", bookingId))
    for (doc in docs) {
        val metadata = doc.get("metadata", Document::class.java) ?: continue
        images.add(
            CheckinImage(
                id = doc.getObjectId("_id").toString(),
                bookingId = metadata.getString("bookingId") ?: continue,
                userId = metadata.getString("userId") ?: "",
                type = metadata.getString("type") ?: "",
                uploadedAt = metadata.getDate("uploadedAt") ?: doc.getDate("uploadDate"),
                filename = doc.getString("filename") ?: "",
                contentType = metadata.getString("contentType") ?: ""
            )
        )
    }

    mongoClient.close()
    return images
}

fun streamCheckinImage(imageId: String, outputStream: java.io.OutputStream) {
    val uri = getMongoDbUri()
    val settings = MongoClientSettings.builder()
        .applyConnectionString(ConnectionString(uri))
        .build()
    val mongoClient = MongoClients.create(settings)

    val database = mongoClient.getDatabase("checkin_images")
    val bucket = com.mongodb.client.gridfs.GridFSBuckets.create(database)

    bucket.downloadToStream(org.bson.types.ObjectId(imageId), outputStream)

    mongoClient.close()
}

fun getCheckinImageMetadata(imageId: String): CheckinImage? {
    val uri = getMongoDbUri()
    val settings = MongoClientSettings.builder()
        .applyConnectionString(ConnectionString(uri))
        .build()
    val mongoClient = MongoClients.create(settings)

    val database = mongoClient.getDatabase("checkin_images")
    val filesCollection = database.getCollection("fs.files")

    val doc = filesCollection.find(Filters.eq("_id", org.bson.types.ObjectId(imageId))).first()
    mongoClient.close()

    doc ?: return null
    val metadata = doc.get("metadata", Document::class.java) ?: return null

    return CheckinImage(
        id = doc.getObjectId("_id").toString(),
        bookingId = metadata.getString("bookingId") ?: "",
        userId = metadata.getString("userId") ?: "",
        type = metadata.getString("type") ?: "",
        uploadedAt = metadata.getDate("uploadedAt") ?: doc.getDate("uploadDate"),
        filename = doc.getString("filename") ?: "",
        contentType = metadata.getString("contentType") ?: ""
    )
}

fun deleteCheckinImage(imageId: String) {
    val uri = getMongoDbUri()
    val settings = MongoClientSettings.builder()
        .applyConnectionString(ConnectionString(uri))
        .build()
    val mongoClient = MongoClients.create(settings)

    val database = mongoClient.getDatabase("checkin_images")
    val bucket = com.mongodb.client.gridfs.GridFSBuckets.create(database)
    bucket.delete(org.bson.types.ObjectId(imageId))

    mongoClient.close()
}
