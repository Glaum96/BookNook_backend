package com.login

import com.login.model.LoginCredentials
import com.login.model.getUserByUsername
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

@Service
class CustomUserDetailsService : UserDetailsService {
    override fun loadUserByUsername(username: String): UserDetails {
        println("-----------------")
        println(username)
        println("-----------------")
        val user: LoginCredentials = getUserByUsername(username) ?: throw UsernameNotFoundException("User not found")
        println("Loaded user: $user")
        return org.springframework.security.core.userdetails.User(user.username, user.password, emptyList())
    }
}