package com.users

import com.bookings.model.Booking
import com.bookings.model.deleteMyBooking
import com.bookings.model.getBookings
import com.bookings.model.postNewBooking
import com.google.gson.Gson
import com.users.model.User
import com.users.model.postNewUser
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/bookings")
@CrossOrigin(origins = ["http://localhost:5173"], allowedHeaders = ["*"], allowCredentials = "true")
class BookingController {
    @GetMapping
    fun getAllBookings(): List<Booking> {
        return getBookings()

    }
}

@RestController
@RequestMapping("/api/postBooking")
@CrossOrigin(origins = ["http://localhost:5173"], allowedHeaders = ["*"], allowCredentials = "true")
class PostBookingController {
    @PostMapping
    fun postBooking(@RequestBody bookingJson: String): String {
        val gson = Gson()
        val booking: Booking = gson.fromJson(bookingJson, Booking::class.java)
        postNewBooking(booking)
        return booking.toString() + " created"
    }

}

@RestController
@RequestMapping("/api/deleteBooking")
@CrossOrigin(origins = ["http://localhost:5173"], allowedHeaders = ["*"], allowCredentials = "true")
class DeleteBookingController {
    @DeleteMapping("/{bookingId}")
    fun deleteBooking(@PathVariable bookingId: String): String {
        return deleteMyBooking(bookingId)
    }

}



