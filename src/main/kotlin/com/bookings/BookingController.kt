package com.users

import com.bookings.model.Booking
import com.bookings.model.deleteBookingInDB
import com.bookings.model.getUserBookingsFromDB
import com.bookings.model.postBookingToDB
import com.google.gson.Gson
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
    fun postBooking(@RequestBody bookingJson: String): String {
        val gson = Gson()
        val booking: Booking = gson.fromJson(bookingJson, Booking::class.java)
        postBookingToDB(booking)
        return booking.toString() + " created"
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
