package com.login

import com.login.model.LoginCredentials
import com.login.model.RegisterUser
import com.login.model.ValidationResult
import com.login.model.getUserByUsername
import com.users.model.getUserFromDB
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class UserService {

    @Autowired
    private lateinit var passwordEncoder: PasswordEncoder

    @Autowired
    private lateinit var passwordValidator: PasswordValidator

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

    fun validateNewUser(user: RegisterUser): ValidationResult {
        val errors = mutableListOf<String>()

        if (user.name.isBlank()) {
            errors.add("Name is required")
        }

        if (user.email.isBlank()) {
            errors.add("Email is required")
        } else {
            val emailHasValidFormat = Regex("^[A-Za-z0-9](.*)([@])(.+)(\\.)(.+)").matches(user.email)
            if (!emailHasValidFormat) {
                errors.add("Invalid email format")
            }

            val emailAddressAlreadyExists = getUserByUsername(user.email) != null
            if (emailAddressAlreadyExists) {
                errors.add("Email address is already registered")
            }
        }

        val passwordResult = passwordValidator.validate(user.password)
        if (!passwordResult.isValid) {
            errors.addAll(passwordResult.errors)
        }

        return if (errors.isEmpty()) {
            ValidationResult.success()
        } else {
            ValidationResult.failure(errors)
        }
    }
}