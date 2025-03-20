package com.users

import com.bookings.model.Booking
import com.bookings.model.deleteBookingInDB
import com.bookings.model.getMyBookingsFromDB
import com.bookings.model.postBookingToDB
import com.google.gson.Gson
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/bookings")
@CrossOrigin(origins = ["http://localhost:5173"], allowedHeaders = ["*"], allowCredentials = "true")
class BookingController {
    @GetMapping
    fun getAllBookings(): List<Booking> {
        return com.bookings.model.getAllBookingsFromDB()

    }
}

@RestController
@RequestMapping("/api/myBookings")
@CrossOrigin(origins = ["http://localhost:5173"], allowedHeaders = ["*"], allowCredentials = "true")
class MyBookingController {
    @GetMapping
    fun getMyBookings(@RequestHeader("User-Id") userId: String): List<Booking> {
        return getMyBookingsFromDB(userId)
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

        return if (postBookingToDB(booking)) {
            "${booking} created"
        } else {
            "Booking time slot is already taken"
        }
    }
}

@RestController
@RequestMapping("/api/deleteBooking")
@CrossOrigin(origins = ["http://localhost:5173"], allowedHeaders = ["*"], allowCredentials = "true")
class DeleteBookingController {
    @DeleteMapping("/{bookingId}")
    fun deleteBooking(@PathVariable bookingId: String): String {
        return deleteBookingInDB(bookingId)
    }

}



