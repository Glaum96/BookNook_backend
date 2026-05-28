package com.checkin.model

import java.util.Date

data class CheckinImage(
    val id: String,
    val bookingId: String,
    val userId: String,
    val type: String,
    val uploadedAt: Date,
    val filename: String,
    val contentType: String
)
