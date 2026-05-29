package com.audit.model

import java.util.Date

enum class AuditAction {
    BOOKING_CREATED,
    BOOKING_DELETED,
    USER_REGISTERED,
    USER_UPDATED,
    USER_DELETED,
    CHECKIN_IMAGE_UPLOADED,
    CHECKOUT_IMAGE_UPLOADED,
    CHECKIN_IMAGE_DELETED
}

data class AuditLog(
    val id: String,
    val timestamp: Date,
    val action: AuditAction,
    val performedByUserId: String,
    val performedByName: String,
    val targetId: String,
    val details: String
)
