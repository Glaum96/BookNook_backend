package com.main.model

import com.mongodb.ConnectionString
import com.mongodb.MongoClientSettings
import com.mongodb.reactivestreams.client.MongoClient
import com.mongodb.reactivestreams.client.MongoClients
import org.bson.codecs.configuration.CodecRegistries
import org.bson.codecs.configuration.CodecRegistry
import org.bson.codecs.pojo.PojoCodecProvider

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