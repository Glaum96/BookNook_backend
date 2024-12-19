package com.users.model

import com.main.model.createMongoClient
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.reactive.awaitFirstOrNull
import org.bson.Document

fun postNewUser(newUser: User) = runBlocking {

    val uri = "mongodb+srv://takterrassen:Seilduksgata6B@takterrassen.jz3qs.mongodb.net/?retryWrites=true&w=majority&appName=Takterrassen"

    val mongoClient = createMongoClient(uri)

    val database = mongoClient.getDatabase("Users")
    val collection = database.getCollection("Users")
    val user = mutableListOf<User>()

    runBlocking {

        val userDocument = Document()
            .append("id", newUser.id)
            .append("name", newUser.name)
            .append("email", newUser.email)
            .append("apartmentNumber", newUser.apartmentNumber)
            .append("phoneNumber", newUser.phoneNumber)

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
    return@runBlocking user
}
