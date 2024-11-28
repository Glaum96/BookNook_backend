package com.users

import com.bookings.model.Booking
import com.bookings.model.getBookings
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
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





