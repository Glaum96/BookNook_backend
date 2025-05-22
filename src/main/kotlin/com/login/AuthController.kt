package com.login

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler
import org.springframework.web.bind.annotation.*
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = ["http://localhost:5173"], allowedHeaders = ["*"], allowCredentials = "true")
class AuthController(val authenticationManager: AuthenticationManager) {

    @Autowired
    private lateinit var userService: UserService

    @Autowired
    private lateinit var tokenService: TokenService

    @PostMapping("/login")
    fun login(@RequestBody loginRequest: LoginRequest): ResponseEntity<LoginResponse> {
        println("Login request received: $loginRequest")
        val user = userService.findUserByUsername(loginRequest.username)
            ?: return ResponseEntity(LoginResponse("User not found"), HttpStatus.NOT_FOUND)

        return if (userService.verifyUserPassword(loginRequest.password, user.password)) {

            var adminToken: String? = null

            if (userService.userIsAdmin(loginRequest.username)) {
                println("User is admin")
                adminToken = tokenService.generateToken(user.username, tokenType = TokenType.ADMIN)
            } else {
                println("User is not admin")
            }

            val authToken = tokenService.generateToken(user.username, tokenType = TokenType.LOGIN)
            ResponseEntity(LoginResponse("Login successful", authToken, adminToken, user.id), HttpStatus.OK)

        } else {
            ResponseEntity(LoginResponse("Invalid credentials"), HttpStatus.UNAUTHORIZED)
        }
    }

    @PostMapping("/logout")
    fun logout(request: HttpServletRequest, response: HttpServletResponse): ResponseEntity<String> {
        val auth = SecurityContextHolder.getContext().authentication
        if (auth != null) {
            SecurityContextLogoutHandler().logout(request, response, auth)
        }
        return ResponseEntity("Logout successful", HttpStatus.OK)
    }

}

data class LoginRequest(val username: String, val password: String)
data class LoginResponse(val message: String, val authToken: String? = null, val adminToken: String? = null, val userId: String? = null)

