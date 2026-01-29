package com.main

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@RestController
@RequestMapping("/")
class MainController {
    @get:GetMapping
    val main: String
        get() = "Hei på deg fra backenden! Klokka er: " +
            LocalDateTime.now().format(DateTimeFormatter.ofPattern("d. MMMM yyyy, HH:mm:ss"))
}
