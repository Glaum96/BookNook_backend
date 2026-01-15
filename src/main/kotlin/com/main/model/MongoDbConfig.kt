package com.main.model

fun getMongoDbUri(): String {
    return System.getenv("MONGODB_URI")
        ?: "mongodb+srv://booknook:***REMOVED***@booknookcluster.eicfcms.mongodb.net/Users?appName=BookNookCluster"
}

