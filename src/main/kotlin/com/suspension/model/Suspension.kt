package com.suspension.model

import java.util.Date

data class Suspension(
    val id: String,
    val userId: String,
    val suspendedFrom: Date,
    val suspendedUntil: Date,
    val reason: String?,
    val createdByUserId: String,
    val createdAt: Date
)
