package com.rules.model

import com.bookings.model.Booking
import com.bookings.model.getUserBookingsFromDB
import com.login.model.ValidationResult
import com.suspension.model.getActiveSuspensionForUser
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.Calendar
import java.util.Date

fun getPeriodStart(rule: Rule): Date {
    val cal = Calendar.getInstance()
    return when (rule.periodType ?: "month") {
        "month" -> {
            cal.set(Calendar.DAY_OF_MONTH, 1)
            cal.set(Calendar.HOUR_OF_DAY, 0); cal.set(Calendar.MINUTE, 0); cal.set(Calendar.SECOND, 0); cal.set(Calendar.MILLISECOND, 0)
            cal.time
        }
        "quarter" -> {
            val month = cal.get(Calendar.MONTH)
            val quarterStart = (month / 3) * 3
            cal.set(Calendar.MONTH, quarterStart)
            cal.set(Calendar.DAY_OF_MONTH, 1)
            cal.set(Calendar.HOUR_OF_DAY, 0); cal.set(Calendar.MINUTE, 0); cal.set(Calendar.SECOND, 0); cal.set(Calendar.MILLISECOND, 0)
            cal.time
        }
        "year" -> {
            cal.set(Calendar.DAY_OF_YEAR, 1)
            cal.set(Calendar.HOUR_OF_DAY, 0); cal.set(Calendar.MINUTE, 0); cal.set(Calendar.SECOND, 0); cal.set(Calendar.MILLISECOND, 0)
            cal.time
        }
        "days" -> {
            val days = rule.periodDays ?: 30
            Date.from(Instant.now().minus(days.toLong(), ChronoUnit.DAYS))
        }
        else -> {
            cal.set(Calendar.DAY_OF_MONTH, 1)
            cal.set(Calendar.HOUR_OF_DAY, 0); cal.set(Calendar.MINUTE, 0); cal.set(Calendar.SECOND, 0); cal.set(Calendar.MILLISECOND, 0)
            cal.time
        }
    }
}

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
                "MAX_HOURS_PER_PERIOD" -> {
                    val periodStart = getPeriodStart(rule)
                    val usedHours = getUserBookingsFromDB(booking.userId, true)
                        .filter { !it.startTime.before(periodStart) }
                        .sumOf { (it.endTime.time - it.startTime.time) / 3_600_000.0 }
                    val bookingHours = (booking.endTime.time - booking.startTime.time) / 3_600_000.0
                    if (usedHours + bookingHours > rule.value) {
                        val usedRounded = String.format("%.1f", usedHours)
                        errors.add("Du har ikke nok kvote igjen denne perioden. Brukt: ${usedRounded}t av ${rule.value}t")
                    }
                }
            }
        }
    }

    val startCal = java.util.Calendar.getInstance().apply { time = booking.startTime }
    val endCal = java.util.Calendar.getInstance().apply { time = booking.endTime }
    val sameDay = startCal.get(java.util.Calendar.YEAR) == endCal.get(java.util.Calendar.YEAR) &&
        startCal.get(java.util.Calendar.DAY_OF_YEAR) == endCal.get(java.util.Calendar.DAY_OF_YEAR)
    if (!sameDay) {
        errors.add("En booking kan ikke strekke seg over flere dager")
    }

    val suspension = getActiveSuspensionForUser(booking.userId)
    if (suspension != null) {
        val fmt = SimpleDateFormat("dd.MM.yyyy")
        errors.add("Du er suspendert fra å opprette bookinger frem til ${fmt.format(suspension.suspendedUntil)}")
    }

    return if (errors.isEmpty()) ValidationResult.success() else ValidationResult.failure(errors)
}
