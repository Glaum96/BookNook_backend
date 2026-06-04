package com.rules.model

import com.main.model.createMongoClient
import com.main.model.getMongoDbUri
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.runBlocking
import org.bson.Document

data class RuleDefinition(val name: String, val description: String, val defaultValue: Int, val defaultPeriodType: String? = null)

private val definitions = mapOf(
    "MAX_ACTIVE_BOOKINGS" to RuleDefinition("Maks aktive bookinger", "Maks antall aktive bookinger per bruker", 2),
    "MAX_BOOKING_FUTURE_DAYS" to RuleDefinition("Maks dager frem i tid", "Hvor langt frem i tid en bruker kan opprette bookinger", 7),
    "MAX_HOURS_PER_PERIOD" to RuleDefinition("Maks timer per periode", "Begrenser antall bookede timer per bruker i en kalenderperiode", 20, "month")
)

fun getRulesFromDB(): List<Rule> = runBlocking {
    val uri = getMongoDbUri()
    val mongoClient = createMongoClient(uri)

    val database = mongoClient.getDatabase("Bookings")
    val collection = database.getCollection("Rules")

    val docs = collection.find().asFlow().toList()

    if (docs.isEmpty()) {
        val seedDocs = definitions.map { (id, def) ->
            val doc = Document()
                .append("id", id)
                .append("enabled", true)
                .append("value", def.defaultValue)
            if (def.defaultPeriodType != null) doc.append("periodType", def.defaultPeriodType)
            doc
        }
        seedDocs.forEach { doc ->
            runBlocking { collection.insertOne(doc).asFlow().toList() }
        }
        mongoClient.close()
        return@runBlocking definitions.map { (id, def) ->
            Rule(id = id, name = def.name, description = def.description, enabled = true, value = def.defaultValue, periodType = def.defaultPeriodType)
        }
    }

    data class RuleState(val enabled: Boolean, val value: Int?, val periodType: String?, val periodDays: Int?)
    val stateById = docs.associate { doc ->
        doc.getString("id") to RuleState(
            enabled = doc.getBoolean("enabled") ?: true,
            value = doc.getInteger("value"),
            periodType = doc.getString("periodType"),
            periodDays = doc.getInteger("periodDays")
        )
    }

    mongoClient.close()

    definitions.map { (id, def) ->
        val state = stateById[id]
        Rule(
            id = id,
            name = def.name,
            description = def.description,
            enabled = state?.enabled ?: true,
            value = state?.value ?: def.defaultValue,
            periodType = state?.periodType ?: def.defaultPeriodType,
            periodDays = state?.periodDays
        )
    }
}
