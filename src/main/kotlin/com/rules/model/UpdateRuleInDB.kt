package com.rules.model

import com.main.model.createMongoClient
import com.main.model.getMongoDbUri
import com.mongodb.client.model.Filters
import com.mongodb.client.model.ReplaceOptions
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.runBlocking
import org.bson.Document

fun updateRuleInDB(ruleId: String, enabled: Boolean) = runBlocking {
    val uri = getMongoDbUri()
    val mongoClient = createMongoClient(uri)

    val database = mongoClient.getDatabase("Bookings")
    val collection = database.getCollection("Rules")

    val filter = Filters.eq("id", ruleId)
    val replacement = Document()
        .append("id", ruleId)
        .append("enabled", enabled)

    collection.replaceOne(filter, replacement, ReplaceOptions().upsert(true)).asFlow().toList()

    mongoClient.close()
}
