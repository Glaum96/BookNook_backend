package com.example.takterrassen_backend

import com.example.takterrassen_backend.model.User
import com.example.takterrassen_backend.model.getUsers
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
        val users = getUsers()

        return listOf(
            User(1.toString(), "John Doe", "john.doe@example.com"),
            User(2.toString(), "Jane Smith", "jane.smith@example.com"),
            users.elementAt(0)
        )
    }
}





