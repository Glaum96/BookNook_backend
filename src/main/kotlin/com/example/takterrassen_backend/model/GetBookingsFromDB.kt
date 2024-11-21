package com.example.takterrassen_backend.model

import com.mongodb.ConnectionString
import com.mongodb.MongoClientSettings
import com.mongodb.reactivestreams.client.MongoClient
import com.mongodb.reactivestreams.client.MongoClients
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.runBlocking
import org.bson.codecs.configuration.CodecRegistries
import org.bson.codecs.configuration.CodecRegistry
import org.bson.codecs.pojo.PojoCodecProvider

fun main() {
    getBookings()
}


fun getBookings() = runBlocking {

    val uri = "mongodb+srv://takterrassen:Seilduksgata6B@takterrassen.jz3qs.mongodb.net/?retryWrites=true&w=majority&appName=Takterrassen"

    val mongoClient = createMongoClient(uri)

    val database = mongoClient.getDatabase("Bookings")
    val collection = database.getCollection("Bookings")
    val bookings = mutableListOf<Booking>()

    runBlocking {
        val docs = collection.find().asFlow().toList()
        for (doc in docs) {
            println(doc.toJson())
            bookings.add(
                Booking(
                    id = "123",
                    from = doc.getString("from"),
                    to = doc.getString("to"),
                    bookerId = doc.getString("bookerId")
                )
            )
        }
    }

    mongoClient.close()
    return@runBlocking bookings
}
