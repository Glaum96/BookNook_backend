package com.rules.model

import com.main.model.createMongoClient
import com.main.model.getMongoDbUri
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.runBlocking
import org.bson.Document

private val definitions = mapOf(
    "MAX_ACTIVE_BOOKINGS" to Triple("Maks aktive bookinger", "Maks antall aktive bookinger per bruker", 2),
    "MAX_BOOKING_FUTURE_DAYS" to Triple("Maks dager frem i tid", "Hvor langt frem i tid en bruker kan opprette bookinger", 7)
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
                .append("value", triple.third)
        }
        seedDocs.forEach { doc ->
            runBlocking { collection.insertOne(doc).asFlow().toList() }
        }
        mongoClient.close()
        return@runBlocking definitions.map { (id, triple) ->
            Rule(id = id, name = triple.first, description = triple.second, enabled = true, value = triple.third)
        }
    }

    val stateById = docs.associate { doc ->
        doc.getString("id") to Pair(
            doc.getBoolean("enabled") ?: true,
            doc.getInteger("value")
        )
    }

    mongoClient.close()

    definitions.map { (id, triple) ->
        val state = stateById[id]
        Rule(
            id = id,
            name = triple.first,
            description = triple.second,
            enabled = state?.first ?: true,
            value = state?.second ?: triple.third
        )
    }
}
