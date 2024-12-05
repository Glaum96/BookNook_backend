package com.login

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.security.Keys
import java.util.*

object TokenService {
    private val key = Keys.hmacShaKeyFor("your-256-biweurhfiuewrhfgiuerhgifuehrigheiurhgit-secret".toByteArray()) // TODO: Use a constant key

    fun generateToken(username: String): String {
        return Jwts.builder()
            .setSubject(username)
            .setIssuedAt(Date())
            .setExpiration(Date(System.currentTimeMillis() + 86400000)) // 1 day expiration
            .signWith(key)
            .compact()
    }

    fun validateToken(token: String): Boolean {
        return try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token)
            true
        } catch (e: Exception) {
            false
        }
    }

    fun getUsernameFromToken(token: String): String {
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).body.subject
    }
}