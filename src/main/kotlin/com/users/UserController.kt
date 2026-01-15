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
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/getUsers")
class GetUsersController {

    @Autowired
    private lateinit var userUtil: UserUtil

    @GetMapping
    fun getAllUsers(@RequestHeader("Authorization") authorizationHeader: String): List<User>? {
        if(userUtil.validateAdminAction(authorizationHeader)) {
            return getUsersFromDb()
        }

        return null
    }
}

@RestController
@RequestMapping("/api/postUser")
class PostUserController {

    @Autowired
    private lateinit var userService: UserService

    @PostMapping
    fun postUser(@RequestBody userJson: String): String {
        val gson = Gson()
        val user: RegisterUser = gson.fromJson(userJson, RegisterUser::class.java)

        val userIsValid = userService.validateNewUser(user)
        if (!userIsValid) {
            return "Invalid user data"
        }

        val encryptedPassword = userService.getEncryptedUserPassword(user.email, user.password)
        postNewUser(user, encryptedPassword)
        return user.toString()
    }
}

@RestController
@RequestMapping("/api/getUser")
class GetUserController {

    @Autowired
    private lateinit var userUtil: UserUtil

    @GetMapping("/{userId}")
    fun getUser(@PathVariable userId: String, @RequestHeader("Authorization") authorizationHeader: String): User? {
        if (userUtil.validateAdminOrSelfAction(userId, authorizationHeader)) {
            return getUserFromDB(userId)
        }

        return null
    }
}

@RestController
@RequestMapping("/api/users")
class UserController {

    @Autowired
    private lateinit var userUtil: UserUtil

    @PutMapping("/{id}")
    fun updateUser(@PathVariable userId: String, @RequestBody updatedUser: User, @RequestHeader("Authorization") authorizationHeader: String): Boolean {
        if (userUtil.validateAdminOrSelfAction(userId, authorizationHeader)) {
            return putUser(userId, updatedUser)
        }

        return false
    }
}

@RestController
@RequestMapping("/api/deleteUser")
class DeleteUserController {

    @Autowired
    private lateinit var userUtil: UserUtil

    @DeleteMapping("/{userId}")
    fun deleteUser(@PathVariable userId: String, @RequestHeader("Authorization") authorizationHeader: String): String? {
        if (userUtil.validateAdminOrSelfAction(userId, authorizationHeader)) {
            return deleteUserFromDB(userId)
        }

        return null
    }
}
