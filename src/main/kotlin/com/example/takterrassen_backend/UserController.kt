package com.example.takterrassen_backend

import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = ["http://localhost:5173"], allowedHeaders = ["*"], allowCredentials = "true")
class UserController {
    @GetMapping
    fun getAllUsers(): List<User> {
        return listOf(
            User(1, "John Doe", "john.doe@example.com"),
            User(2, "Jane Smith", "jane.smith@example.com")
        )
    }
}

