package com.login

import com.login.model.LoginCredentials
import com.login.model.RegisterUser
import com.login.model.getUserByUsername
import com.users.model.getUserFromDB
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class UserService {

    @Autowired
    private lateinit var passwordEncoder: PasswordEncoder

    fun getEncryptedUserPassword(username: String, rawPassword: String): String {
        val encryptedPassword = passwordEncoder.encode(rawPassword) ?: throw Exception("Password encryption failed")
        return encryptedPassword
    }

    fun verifyUserPassword(rawPassword: String, encodedPassword: String): Boolean {
        return passwordEncoder.matches(rawPassword, encodedPassword)
    }

    fun findUserByUsername(username: String): LoginCredentials? {
        return getUserByUsername(username)
    }

    fun userIsAdmin(userName: String): Boolean {
        return getUserFromDB(userName)?.isAdmin ?: false
    }

    fun validateNewUser(user: RegisterUser): Boolean {
        if (user.name.isBlank() || user.email.isBlank() || user.password.isBlank()) {
            return false
        }

        val emailAddressAlreadyExists = getUserByUsername(user.email) != null
        if (emailAddressAlreadyExists) {
            return false
        }

        val emailHasValidFormat = Regex("^[A-Za-z0-9](.*)([@])(.+)(\\.)(.+)").matches(user.email)
        if (!emailHasValidFormat) {
            return false
        }

        // Additional validation logic can be added here (e.g., email format, password strength)
        return true
    }
}