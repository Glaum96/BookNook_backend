package com.bookings.model

import java.util.Date

data class Booking(
    val id: String,
    val startTime: Date,
    val endTime: Date,
    val userId: String,
    val responsibleName: String,
    val responsibleNumber: String
)