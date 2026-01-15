package com.users.model

import com.login.model.LoginCredentials
import com.login.model.RegisterUser
import com.main.model.createMongoClient
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.reactive.awaitFirstOrNull
import org.bson.Document
import org.bson.types.ObjectId
import org.springframework.data.mongodb.core.mapping.Encrypted
import kotlin.math.log

fun postNewUser(newUser: RegisterUser, encryptedPassword: String) = runBlocking {

    val uri = "mongodb+srv://booknook:bE5uEVvQYfturR2V@booknookcluster.eicfcms.mongodb.net/?appName=BookNookCluster"

    val mongoClient = createMongoClient(uri)

    val database = mongoClient.getDatabase("Users")
    val usersCollection = database.getCollection("Users")
    val loginCredentialsCollection = database.getCollection("LoginCredentials")
    val id = ObjectId().toString()

    runBlocking {

        val loginCredentialsDocument = Document()
            .append("id", id)
            .append("username", newUser.email)
            .append("password", encryptedPassword)

        val userDocument = Document()
            .append("loginObjectId", id)
            .append("name", newUser.name)
            .append("email", newUser.email)
            .append("apartmentNumber", newUser.apartmentNumber)
            .append("phoneNumber", newUser.phoneNumber)
            .append("isAdmin", false)

        usersCollection.insertOne(userDocument).awaitFirstOrNull()
        loginCredentialsCollection.insertOne(loginCredentialsDocument).awaitFirstOrNull()

    }

    mongoClient.close()
    return@runBlocking
}
