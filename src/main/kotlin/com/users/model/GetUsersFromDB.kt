package com.users.model

import com.main.model.createMongoClient
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.runBlocking

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


