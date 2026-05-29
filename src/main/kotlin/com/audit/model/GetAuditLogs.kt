package com.audit.model

import com.main.model.getMongoDbUri
import com.mongodb.ConnectionString
import com.mongodb.MongoClientSettings
import com.mongodb.client.MongoClients
import com.mongodb.client.model.Sorts

fun getAuditLogs(): List<AuditLog> {
    val uri = getMongoDbUri()
    val settings = MongoClientSettings.builder()
        .applyConnectionString(ConnectionString(uri))
        .build()
    val mongoClient = MongoClients.create(settings)

    val logs = mutableListOf<AuditLog>()
    val docs = mongoClient.getDatabase("audit")
        .getCollection("logs")
        .find()
        .sort(Sorts.descending("timestamp"))

    for (doc in docs) {
        logs.add(
            AuditLog(
                id = doc.getObjectId("_id").toString(),
                timestamp = doc.getDate("timestamp"),
                action = AuditAction.valueOf(doc.getString("action")),
                performedByUserId = doc.getString("performedByUserId") ?: "",
                performedByName = doc.getString("performedByName") ?: "",
                targetId = doc.getString("targetId") ?: "",
                details = doc.getString("details") ?: ""
            )
        )
    }

    mongoClient.close()
    return logs
}
