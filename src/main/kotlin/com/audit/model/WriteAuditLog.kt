package com.audit.model

import com.main.model.getMongoDbUri
import com.mongodb.ConnectionString
import com.mongodb.MongoClientSettings
import com.mongodb.client.MongoClients
import org.bson.Document
import java.util.Date

fun writeAuditLog(
    action: AuditAction,
    performedByUserId: String,
    performedByName: String,
    targetId: String,
    details: String
) {
    try {
        val uri = getMongoDbUri()
        val settings = MongoClientSettings.builder()
            .applyConnectionString(ConnectionString(uri))
            .build()
        val mongoClient = MongoClients.create(settings)

        val doc = Document()
            .append("timestamp", Date())
            .append("action", action.name)
            .append("performedByUserId", performedByUserId)
            .append("performedByName", performedByName)
            .append("targetId", targetId)
            .append("details", details)

        mongoClient.getDatabase("audit").getCollection("logs").insertOne(doc)
        mongoClient.close()
    } catch (e: Exception) {
        println("Audit logging failed: ${e.message}")
    }
}
