package com.audit

import com.audit.model.AuditAction
import com.audit.model.writeAuditLog
import com.login.TokenService
import com.login.UserService
import com.users.model.getUserFromDB
import org.springframework.stereotype.Service

@Service
class AuditService(
    private val tokenService: TokenService,
    private val userService: UserService
) {
    fun log(
        authHeader: String,
        action: AuditAction,
        targetId: String,
        details: String
    ) {
        try {
            val token = authHeader.removePrefix("Bearer ").trim()
            val username = tokenService.getUsernameFromToken(token)
            val credentials = userService.findUserByUsername(username) ?: return
            val user = getUserFromDB(credentials.id)
            val displayName = user?.name ?: username
            writeAuditLog(action, credentials.id, displayName, targetId, details)
        } catch (e: Exception) {
            println("AuditService.log failed: ${e.message}")
        }
    }
}
