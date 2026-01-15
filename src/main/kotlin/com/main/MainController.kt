package com.main

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/")
class MainController {
    @get:GetMapping
    val main: String
        get() = "Hei på deg fra backenden!"
}
