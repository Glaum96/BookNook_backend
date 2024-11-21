package com.example.takterrassen_backend.model

import com.mongodb.ConnectionString
import com.mongodb.MongoClientSettings
import com.mongodb.client.model.Filters.eq
import com.mongodb.reactivestreams.client.MongoClient
import com.mongodb.reactivestreams.client.MongoClients
import kotlinx.coroutines.runBlocking
import org.bson.codecs.configuration.CodecRegistries
import org.bson.codecs.configuration.CodecRegistry
import org.bson.codecs.pojo.PojoCodecProvider
import org.bson.Document
import kotlinx.coroutines.reactive.awaitFirstOrNull

fun main() = runBlocking {

    val uri = "mongodb+srv://takterrassen:Seilduksgata6B@takterrassen.jz3qs.mongodb.net/?retryWrites=true&w=majority&appName=Takterrassen"

    val mongoClient = createMongoClient(uri)

    val database = mongoClient.getDatabase("Users")
    // Get a collection of documents of type User
    val collection = database.getCollection("Users")

    runBlocking {
        val doc = collection.find(eq("name", "Jane Doe")).awaitFirstOrNull()
        if (doc != null) {
            println(doc.toJson())
            println("YAAAY! We found a matching document.")
        } else {
            println("No matching documents found.")
        }
    }

    mongoClient.close()
}

fun createMongoClient(uri: String): MongoClient {
    val pojoCodecProvider = PojoCodecProvider.builder().automatic(true).build()
    val codecRegistry: CodecRegistry = CodecRegistries.fromRegistries(
        MongoClientSettings.getDefaultCodecRegistry(),
        CodecRegistries.fromProviders(pojoCodecProvider)
    )

    val settings = MongoClientSettings.builder()
        .applyConnectionString(ConnectionString(uri))
        .codecRegistry(codecRegistry)
        .build()

    return MongoClients.create(settings)
}