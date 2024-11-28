package com.takterrassen_backend.model

import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.reactive.awaitFirstOrNull
import org.bson.Document

fun postNewUser(newUser: User) = runBlocking {

    val uri = "mongodb+srv://takterrassen:Seilduksgata6B@takterrassen.jz3qs.mongodb.net/?retryWrites=true&w=majority&appName=Takterrassen"

    val mongoClient = createMongoClient(uri)

    val database = mongoClient.getDatabase("Users")
    val collection = database.getCollection("Users")
    val users = mutableListOf<User>()

    runBlocking {

        val userDocument = Document()
            .append("id", newUser.id)
            .append("name", newUser.name)
            .append("email", newUser.email)

        collection.insertOne(userDocument).awaitFirstOrNull()


//        val docs = collection.find().asFlow().toList()
//        for (doc in docs) {
//            println(doc.toJson())
//            users.add(
//                User(
//                    id = "123",
//                    name = doc.getString("name"),
//                    email = doc.getString("email")
//                )
//            )
//
//        }
    }

    mongoClient.close()
    return@runBlocking users
}
