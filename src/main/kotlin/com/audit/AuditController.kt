package com.audit

import com.audit.model.AuditLog
import com.audit.model.getAuditLogs
import com.users.UserUtil
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/audit")
class AuditController(private val userUtil: UserUtil) {

    @GetMapping
    fun getAuditLogs(
        @RequestHeader("Authorization") authHeader: String
    ): ResponseEntity<List<AuditLog>> {
        if (!userUtil.validateAdminAction(authHeader)) {
            return ResponseEntity(HttpStatus.FORBIDDEN)
        }
        return ResponseEntity.ok(getAuditLogs())
    }
}
