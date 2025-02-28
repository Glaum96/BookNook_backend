package com.users

import com.users.model.User
import com.users.model.postNewUser
import com.google.gson.Gson
import com.login.UserService
import com.login.model.RegisterUser
import com.users.model.deleteUserFromDB
import com.users.model.getUserFromDB
import com.users.model.getUsersFromDb
import com.users.model.putUser
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.DeleteMapping
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
@RequestMapping("/api/postUser")
@CrossOrigin(origins = ["http://localhost:5173"], allowedHeaders = ["*"], allowCredentials = "true")
class PostUserController {

    @Autowired
    private lateinit var userService: UserService

    @PostMapping
    fun postUser(@RequestBody userJson: String): String {
        val gson = Gson()
        val user: RegisterUser = gson.fromJson(userJson, RegisterUser::class.java)
        val encryptedPassword = userService.getEncryptedUserPassword(user.email, user.password)
        postNewUser(user, encryptedPassword)
        return user.toString()
    }
}

@RestController
@RequestMapping("/api/getUser")
@CrossOrigin(origins = ["http://localhost:5173"], allowedHeaders = ["*"], allowCredentials = "true")
class GetUserController {

    @GetMapping("/{userId}")
    fun getUser(@PathVariable userId: String): User? {
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

@RestController
@RequestMapping("/api/deleteUser")
@CrossOrigin(origins = ["http://localhost:5173"], allowedHeaders = ["*"], allowCredentials = "true")
class DeleteUserController {
    @DeleteMapping("/{userId}")
    fun deleteUser(@PathVariable userId: String): String {
        return deleteUserFromDB(userId)
    }
}







