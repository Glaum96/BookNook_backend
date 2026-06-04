package com.users

import com.audit.AuditService
import com.audit.model.AuditAction
import com.bookings.model.Booking
import com.bookings.model.deleteBookingInDB
import com.bookings.model.getBookingFromDB
import com.bookings.model.getUserBookingsFromDB
import com.bookings.model.postBookingToDB
import com.bookings.model.putBookingInDB
import com.google.gson.Gson
import com.rules.model.validateBookingAgainstRules
import com.users.UserUtil
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/bookings")
class BookingController {
    @GetMapping
    fun getAllBookings(): List<Booking> {
        return com.bookings.model.getAllBookingsFromDB()
    }
}

@RestController
@RequestMapping("/api/myBookings")
class MyBookingController {
    @GetMapping
    fun getMyBookings(@RequestHeader("User-Id") userId: String, @RequestHeader("includePastBookings") includePastBookings: Boolean): List<Booking> {
        return getUserBookingsFromDB(userId, includePastBookings)
    }
}

@RestController
@RequestMapping("/api/postBooking")
class PostBookingController {

    @Autowired
    private lateinit var auditService: AuditService

    @PostMapping
    fun postBooking(
        @RequestBody bookingJson: String,
        @RequestHeader("Authorization") authHeader: String
    ): ResponseEntity<Map<String, Any>> {
        val booking: Booking = Gson().fromJson(bookingJson, Booking::class.java)
        val validation = validateBookingAgainstRules(booking)
        if (!validation.isValid) {
            return ResponseEntity(mapOf("success" to false, "errors" to validation.errors), HttpStatus.BAD_REQUEST)
        }
        postBookingToDB(booking)
        auditService.log(authHeader, AuditAction.BOOKING_CREATED, booking.userId, "Booking opprettet: ${booking.startTime} – ${booking.endTime}")
        return ResponseEntity(mapOf("success" to true), HttpStatus.CREATED)
    }
}

@RestController
@RequestMapping("/api/updateBooking")
class UpdateBookingController {

    @Autowired
    private lateinit var auditService: AuditService

    @Autowired
    private lateinit var userUtil: UserUtil

    @PutMapping("/{bookingId}")
    fun updateBooking(
        @PathVariable bookingId: String,
        @RequestBody bookingJson: String,
        @RequestHeader("Authorization") authHeader: String
    ): ResponseEntity<Map<String, Any>> {
        val existing = getBookingFromDB(bookingId)
            ?: return ResponseEntity(mapOf("success" to false, "errors" to listOf("Booking ikke funnet")), HttpStatus.NOT_FOUND)

        val isAdmin = userUtil.validateAdminAction(authHeader)
        val isOwner = userUtil.requestingUserIsSameAsTargetUser(existing.userId, authHeader)
        if (!isAdmin && !isOwner) {
            return ResponseEntity(mapOf("success" to false, "errors" to listOf("Ingen tilgang")), HttpStatus.FORBIDDEN)
        }

        val updated: com.bookings.model.Booking = Gson().fromJson(bookingJson, com.bookings.model.Booking::class.java)
        val validation = validateBookingAgainstRules(updated)
        if (!validation.isValid) {
            return ResponseEntity(mapOf("success" to false, "errors" to validation.errors), HttpStatus.BAD_REQUEST)
        }

        val success = putBookingInDB(bookingId, updated)
        if (success) {
            auditService.log(authHeader, AuditAction.BOOKING_UPDATED, bookingId, "Booking oppdatert: ${updated.startTime} – ${updated.endTime}")
        }
        return if (success) ResponseEntity(mapOf("success" to true), HttpStatus.OK)
        else ResponseEntity(mapOf("success" to false, "errors" to listOf("Oppdatering feilet")), HttpStatus.INTERNAL_SERVER_ERROR)
    }
}

@RestController
@RequestMapping("/api/deleteBooking")
class DeleteBookingController {

    @Autowired
    private lateinit var auditService: AuditService

    @DeleteMapping("/{bookingId}")
    fun deleteBooking(
        @PathVariable bookingId: String,
        @RequestHeader("Authorization") authHeader: String
    ): String {
        auditService.log(authHeader, AuditAction.BOOKING_DELETED, bookingId, "Booking slettet")
        return deleteBookingInDB(bookingId)
    }
}
