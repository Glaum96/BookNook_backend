package com.rules.model

import com.main.model.createMongoClient
import com.main.model.getMongoDbUri
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.runBlocking
import org.bson.Document

private val definitions = mapOf(
    "MAX_ACTIVE_BOOKINGS" to Triple("Maks aktive bookinger", "En bruker kan ha maks 2 aktive bookinger om gangen", 2),
    "MAX_BOOKING_FUTURE_DAYS" to Triple("Maks dager frem i tid", "En booking kan maks opprettes 7 dager frem i tid", 7)
)

fun getRulesFromDB(): List<Rule> = runBlocking {
    val uri = getMongoDbUri()
    val mongoClient = createMongoClient(uri)

    val database = mongoClient.getDatabase("Bookings")
    val collection = database.getCollection("Rules")

    val docs = collection.find().asFlow().toList()

    if (docs.isEmpty()) {
        val seedDocs = definitions.map { (id, triple) ->
            Document()
                .append("id", id)
                .append("enabled", true)
        }
        seedDocs.forEach { doc ->
            runBlocking { collection.insertOne(doc).asFlow().toList() }
        }
        mongoClient.close()
        return@runBlocking definitions.map { (id, triple) ->
            Rule(id = id, name = triple.first, description = triple.second, enabled = true, value = triple.third)
        }
    }

    val enabledById = docs.associate { doc ->
        doc.getString("id") to (doc.getBoolean("enabled") ?: true)
    }

    mongoClient.close()

    definitions.map { (id, triple) ->
        Rule(
            id = id,
            name = triple.first,
            description = triple.second,
            enabled = enabledById[id] ?: true,
            value = triple.third
        )
    }
}
