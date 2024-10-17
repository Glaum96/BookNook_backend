package com.example.takterrassen_backend

import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = ["http://localhost:5173"])
class UserController {
    @get:GetMapping
    val allUsers: String
        get() =  "Users: "
}