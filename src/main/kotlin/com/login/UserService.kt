package com.login

import com.login.model.LoginCredentials
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
}