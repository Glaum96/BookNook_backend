package com.takterrassen_backend.model

import com.mongodb.ConnectionString
import com.mongodb.MongoClientSettings
import com.mongodb.reactivestreams.client.MongoClient
import com.mongodb.reactivestreams.client.MongoClients
import kotlinx.coroutines.runBlocking
import org.bson.codecs.configuration.CodecRegistries
import org.bson.codecs.configuration.CodecRegistry
import org.bson.codecs.pojo.PojoCodecProvider
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.reactive.asFlow

fun main() {
    getUsers()
}

fun getUsers() = runBlocking {

    val uri = "mongodb+srv://takterrassen:Seilduksgata6B@takterrassen.jz3qs.mongodb.net/?retryWrites=true&w=majority&appName=Takterrassen"

    val mongoClient = createMongoClient(uri)

    val database = mongoClient.getDatabase("Users")
    val collection = database.getCollection("Users")
    val users = mutableListOf<User>()

    runBlocking {
        val docs = collection.find().asFlow().toList()
        for (doc in docs) {
            println(doc.toJson())
            users.add(
                User(
                    id = "123",
                    name = doc.getString("name"),
                    email = doc.getString("email")
                )
            )

        }
    }

    mongoClient.close()
    return@runBlocking users
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