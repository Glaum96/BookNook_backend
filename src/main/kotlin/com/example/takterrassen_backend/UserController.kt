package com.example.takterrassen_backend

import com.example.takterrassen_backend.model.User
import com.example.takterrassen_backend.model.getUsers
import com.example.takterrassen_backend.model.postNewUser
import com.google.gson.Gson
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/getUsers")
@CrossOrigin(origins = ["http://localhost:5173"], allowedHeaders = ["*"], allowCredentials = "true")
class GetUserController {
    @GetMapping
    fun getAllUsers(): List<User> {
        return  getUsers()
    }

}
@RestController
@RequestMapping("/api/postUsers")
@CrossOrigin(origins = ["http://localhost:5173"], allowedHeaders = ["*"], allowCredentials = "true")
class PostUserController {
    @PostMapping
    fun postUser(@RequestBody userJson: String): String {
        val gson = Gson()
        val user: User = gson.fromJson(userJson, User::class.java)
        postNewUser(user)
        return user.toString()
    }

}







