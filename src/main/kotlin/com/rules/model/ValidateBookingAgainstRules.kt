package com.rules.model

import com.bookings.model.Booking
import com.bookings.model.getUserBookingsFromDB
import com.login.model.ValidationResult
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.Date

fun validateBookingAgainstRules(booking: Booking): ValidationResult {
    val rules = getRulesFromDB()
    val errors = mutableListOf<String>()

    rules.forEach { rule ->
        if (rule.enabled) {
            when (rule.id) {
                "MAX_ACTIVE_BOOKINGS" -> {
                    val activeCount = getUserBookingsFromDB(booking.userId, false).size
                    if (activeCount >= rule.value)
                        errors.add("Du kan ikke ha mer enn ${rule.value} aktive bookinger samtidig")
                }
                "MAX_BOOKING_FUTURE_DAYS" -> {
                    val maxDate = Date.from(Instant.now().plus(rule.value.toLong(), ChronoUnit.DAYS))
                    if (booking.startTime.after(maxDate))
                        errors.add("Bookinger kan ikke opprettes mer enn ${rule.value} dager frem i tid")
                }
            }
        }
    }

    return if (errors.isEmpty()) ValidationResult.success() else ValidationResult.failure(errors)
}
