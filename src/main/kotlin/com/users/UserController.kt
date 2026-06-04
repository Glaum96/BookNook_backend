package com.users

import com.audit.AuditService
import com.audit.model.AuditAction
import com.users.model.User
import com.users.model.postNewUser
import com.google.gson.Gson
import com.login.UserService
import com.login.model.RegisterUser
import com.users.model.deleteUserFromDB
import com.users.model.getUserFromDB
import com.users.model.getUsersFromDb
import com.users.model.putUser
import com.users.model.setUserAdmin
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
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
@RequestMapping("/api/checkEmail")
class CheckEmailController {

    @Autowired
    private lateinit var userService: UserService

    @GetMapping
    fun checkEmail(@RequestParam email: String): ResponseEntity<Map<String, Any>> {
        val exists = userService.findUserByUsername(email) != null
        return if (exists) {
            ResponseEntity(mapOf("available" to false), HttpStatus.CONFLICT)
        } else {
            ResponseEntity(mapOf("available" to true), HttpStatus.OK)
        }
    }
}

@RestController
@RequestMapping("/api/postUser")
class PostUserController {

    @Autowired
    private lateinit var userService: UserService

    @PostMapping
    fun postUser(@RequestBody userJson: String): ResponseEntity<Map<String, Any>> {
        val gson = Gson()
        val user: RegisterUser = gson.fromJson(userJson, RegisterUser::class.java)

        val validationResult = userService.validateNewUser(user)
        if (!validationResult.isValid) {
            return ResponseEntity(
                mapOf(
                    "success" to false,
                    "errors" to validationResult.errors
                ),
                HttpStatus.BAD_REQUEST
            )
        }

        val encryptedPassword = userService.getEncryptedUserPassword(user.email, user.password)
        postNewUser(user, encryptedPassword)

        com.audit.model.writeAuditLog(
            AuditAction.USER_REGISTERED,
            performedByUserId = "",
            performedByName = user.name,
            targetId = user.email,
            details = "Ny bruker registrert: ${user.name} (${user.email})"
        )

        return ResponseEntity(
            mapOf(
                "success" to true,
                "message" to "User registered successfully"
            ),
            HttpStatus.CREATED
        )
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

    @Autowired
    private lateinit var auditService: AuditService

    @PutMapping("/{id}")
    fun updateUser(@PathVariable("id") userId: String, @RequestBody updatedUser: User, @RequestHeader("Authorization") authorizationHeader: String): Boolean {
        if (userUtil.validateAdminOrSelfAction(userId, authorizationHeader)) {
            val result = putUser(userId, updatedUser)
            if (result) auditService.log(authorizationHeader, AuditAction.USER_UPDATED, userId, "Brukerprofil oppdatert")
            return result
        }

        return false
    }
}

@RestController
@RequestMapping("/api/users")
class SetAdminController {

    @Autowired
    private lateinit var userUtil: UserUtil

    @Autowired
    private lateinit var auditService: AuditService

    @PutMapping("/{id}/admin")
    fun setAdmin(
        @PathVariable id: String,
        @RequestBody body: Map<String, Boolean>,
        @RequestHeader("Authorization") authorizationHeader: String
    ): ResponseEntity<Map<String, Any>> {
        if (!userUtil.validateAdminAction(authorizationHeader)) {
            return ResponseEntity(mapOf("success" to false, "error" to "Ingen tilgang"), HttpStatus.FORBIDDEN)
        }
        if (userUtil.requestingUserIsSameAsTargetUser(id, authorizationHeader)) {
            return ResponseEntity(mapOf("success" to false, "error" to "Kan ikke endre egen admin-status"), HttpStatus.FORBIDDEN)
        }
        val isAdmin = body["isAdmin"] ?: return ResponseEntity(mapOf("success" to false, "error" to "Mangler isAdmin-felt"), HttpStatus.BAD_REQUEST)
        val result = setUserAdmin(id, isAdmin)
        if (result) {
            val label = if (isAdmin) "gitt admin-tilgang" else "fjernet admin-tilgang"
            auditService.log(authorizationHeader, AuditAction.USER_ADMIN_CHANGED, id, "Bruker $label")
        }
        return if (result) ResponseEntity(mapOf("success" to true), HttpStatus.OK)
        else ResponseEntity(mapOf("success" to false, "error" to "Oppdatering feilet"), HttpStatus.INTERNAL_SERVER_ERROR)
    }
}

@RestController
@RequestMapping("/api/deleteUser")
class DeleteUserController {

    @Autowired
    private lateinit var userUtil: UserUtil

    @Autowired
    private lateinit var auditService: AuditService

    @DeleteMapping("/{userId}")
    fun deleteUser(@PathVariable userId: String, @RequestHeader("Authorization") authorizationHeader: String): String? {
        if (userUtil.validateAdminOrSelfAction(userId, authorizationHeader)) {
            auditService.log(authorizationHeader, AuditAction.USER_DELETED, userId, "Bruker slettet")
            return deleteUserFromDB(userId)
        }

        return null
    }
}
