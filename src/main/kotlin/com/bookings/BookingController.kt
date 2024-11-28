package com.users

import com.bookings.model.Booking
import com.bookings.model.getBookings
import com.bookings.model.postNewBooking
import com.google.gson.Gson
import com.users.model.User
import com.users.model.postNewUser
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

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
        println("----------------------------------------------------------------------")

        println(booking)
        println(bookingJson)
        postNewBooking(booking)
        println("----------------------------------------------------------------------")

        return booking.toString()
    }

}





