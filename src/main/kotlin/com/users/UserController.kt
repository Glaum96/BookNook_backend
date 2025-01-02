package com.users

import com.users.model.User
import com.users.model.postNewUser
import com.google.gson.Gson
import com.users.model.getUserFromDB
import com.users.model.getUsersFromDb
import com.users.model.putUser
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/getUsers")
@CrossOrigin(origins = ["http://localhost:5173"], allowedHeaders = ["*"], allowCredentials = "true")
class GetUsersController {
    @GetMapping
    fun getAllUsers(): List<User> {
        return  getUsersFromDb()
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

@RestController
@RequestMapping("/api/getUser")
@CrossOrigin(origins = ["http://localhost:5173"], allowedHeaders = ["*"], allowCredentials = "true")
class GetUserController {

    @GetMapping("/{userId}")
    fun getUser(@PathVariable userId: String): User? {
        println("getuser")
        return getUserFromDB(userId)
    }
}

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = ["http://localhost:5173"], allowedHeaders = ["*"], allowCredentials = "true")
class UserController {

    @PutMapping("/{id}")
    fun updateUser(@PathVariable id: String, @RequestBody updatedUser: User): Boolean {
        return putUser(id, updatedUser)
    }
}






