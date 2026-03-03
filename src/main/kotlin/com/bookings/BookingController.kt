package com.users

import com.bookings.model.Booking
import com.bookings.model.deleteBookingInDB
import com.bookings.model.getUserBookingsFromDB
import com.bookings.model.postBookingToDB
import com.google.gson.Gson
import com.rules.model.validateBookingAgainstRules
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
    @PostMapping
    fun postBooking(@RequestBody bookingJson: String): ResponseEntity<Map<String, Any>> {
        val booking: Booking = Gson().fromJson(bookingJson, Booking::class.java)
        val validation = validateBookingAgainstRules(booking)
        if (!validation.isValid) {
            return ResponseEntity(mapOf("success" to false, "errors" to validation.errors), HttpStatus.BAD_REQUEST)
        }
        postBookingToDB(booking)
        return ResponseEntity(mapOf("success" to true), HttpStatus.CREATED)
    }

}

@RestController
@RequestMapping("/api/deleteBooking")
class DeleteBookingController {
    @DeleteMapping("/{bookingId}")
    fun deleteBooking(@PathVariable bookingId: String): String {
        return deleteBookingInDB(bookingId)
    }

}
