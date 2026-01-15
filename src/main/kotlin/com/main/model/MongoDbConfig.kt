package com.main.model

fun getMongoDbUri(): String {
    return System.getenv("MONGODB_URI")
        ?: "mongodb+srv://booknook:bE5uEVvQYfturR2V@booknookcluster.eicfcms.mongodb.net/Users?appName=BookNookCluster"
}

