package com.login

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.stereotype.Service
import java.util.*

@Service
class TokenService {
    private val loginKey = Keys.hmacShaKeyFor("your-256-biweurhfiuewrhfgiuerhgifuehrigheiurhgit-secret".toByteArray()) // TODO: Use a constant key
    private val adminKey = Keys.hmacShaKeyFor("qwertyuqwertyuiwertyuiwertyuioqwertyuiopoiuytrewertyuii".toByteArray()) // TODO: Use a constant key

    fun generateToken(username: String, tokenType: TokenType): String {
        val key = if (tokenType === TokenType.ADMIN) adminKey else loginKey
        return Jwts.builder()
            .setSubject(username)
            .setIssuedAt(Date())
            .setExpiration(Date(System.currentTimeMillis() + 60 * 60 * 24 * 1000)) // 1 day expiration (in milliseconds)
            .signWith(key)
            .compact()
    }

    fun validateToken(token: String, tokenType: TokenType): Boolean {
        return try {
            val key = if (tokenType === TokenType.ADMIN) adminKey else loginKey
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token)
            true
        } catch (e: Exception) {
            false
        }
    }

    fun getUsernameFromToken(token: String): String {
        return Jwts.parserBuilder().setSigningKey(loginKey).build().parseClaimsJws(token).body.subject
    }
}

enum class TokenType {
    LOGIN, ADMIN
}