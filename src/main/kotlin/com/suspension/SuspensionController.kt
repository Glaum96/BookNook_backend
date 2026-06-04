package com.suspension

import com.audit.AuditService
import com.audit.model.AuditAction
import com.bookings.model.getAllBookingsFromDB
import com.login.TokenService
import com.login.UserService
import com.suspension.model.Suspension
import com.suspension.model.createSuspension
import com.suspension.model.deleteActiveSuspension
import com.suspension.model.getAllActiveSuspensions
import com.suspension.model.getActiveSuspensionForUser
import com.suspension.model.updateActiveSuspension
import com.users.UserUtil
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.Date

data class CreateSuspensionRequest(
    val suspendedUntil: Date,
    val reason: String? = null
)

data class UpdateSuspensionRequest(
    val suspendedUntil: Date,
    val reason: String? = null
)

@RestController
@RequestMapping("/api/users/{id}/suspend")
class SuspensionController {

    @Autowired
    private lateinit var userUtil: UserUtil

    @Autowired
    private lateinit var auditService: AuditService

    @Autowired
    private lateinit var tokenService: TokenService

    @Autowired
    private lateinit var userService: UserService

    @PostMapping
    fun createSuspension(
        @PathVariable id: String,
        @RequestBody body: CreateSuspensionRequest,
        @RequestHeader("Authorization") authHeader: String
    ): ResponseEntity<Map<String, Any>> {
        if (!userUtil.validateAdminAction(authHeader)) {
            return ResponseEntity(mapOf("success" to false, "error" to "Ingen tilgang"), HttpStatus.FORBIDDEN)
        }

        val existing = getActiveSuspensionForUser(id)
        if (existing != null) {
            return ResponseEntity(
                mapOf("success" to false, "error" to "Brukeren har allerede en aktiv suspensjon"),
                HttpStatus.CONFLICT
            )
        }

        val adminUserId = resolveUserId(authHeader)
        val suspension = createSuspension(id, body.suspendedUntil, body.reason, adminUserId)

        val now = Date()
        val affectedBookings = getAllBookingsFromDB().filter { booking ->
            booking.userId == id &&
            booking.startTime.before(body.suspendedUntil) &&
            booking.endTime.after(now)
        }

        auditService.log(authHeader, AuditAction.USER_SUSPENDED, id,
            "Suspendert frem til ${body.suspendedUntil}${if (body.reason != null) ": ${body.reason}" else ""}")

        return ResponseEntity(
            mapOf("success" to true, "suspension" to suspension, "affectedBookings" to affectedBookings),
            HttpStatus.CREATED
        )
    }

    @DeleteMapping
    fun deleteSuspension(
        @PathVariable id: String,
        @RequestHeader("Authorization") authHeader: String
    ): ResponseEntity<Map<String, Any>> {
        if (!userUtil.validateAdminAction(authHeader)) {
            return ResponseEntity(mapOf("success" to false, "error" to "Ingen tilgang"), HttpStatus.FORBIDDEN)
        }

        val result = deleteActiveSuspension(id)
        if (result) {
            auditService.log(authHeader, AuditAction.USER_UNSUSPENDED, id, "Suspensjon fjernet")
        }
        return if (result) ResponseEntity(mapOf("success" to true), HttpStatus.OK)
        else ResponseEntity(mapOf("success" to false, "error" to "Ingen aktiv suspensjon funnet"), HttpStatus.NOT_FOUND)
    }

    @PatchMapping
    fun updateSuspension(
        @PathVariable id: String,
        @RequestBody body: UpdateSuspensionRequest,
        @RequestHeader("Authorization") authHeader: String
    ): ResponseEntity<Map<String, Any>> {
        if (!userUtil.validateAdminAction(authHeader)) {
            return ResponseEntity(mapOf("success" to false, "error" to "Ingen tilgang"), HttpStatus.FORBIDDEN)
        }

        val result = updateActiveSuspension(id, body.suspendedUntil, body.reason)
        return if (result) ResponseEntity(mapOf("success" to true), HttpStatus.OK)
        else ResponseEntity(mapOf("success" to false, "error" to "Ingen aktiv suspensjon funnet"), HttpStatus.NOT_FOUND)
    }

    private fun resolveUserId(authHeader: String): String {
        val token = authHeader.removePrefix("Bearer ").trim()
        val username = tokenService.getUsernameFromToken(token)
        return userService.findUserByUsername(username)?.id ?: ""
    }
}

@RestController
@RequestMapping("/api/suspensions")
class AllSuspensionsController {

    @Autowired
    private lateinit var userUtil: UserUtil

    @GetMapping
    fun getAllSuspensions(
        @RequestHeader("Authorization") authHeader: String
    ): ResponseEntity<List<Suspension>> {
        if (!userUtil.validateAdminAction(authHeader)) {
            return ResponseEntity(null, HttpStatus.FORBIDDEN)
        }
        return ResponseEntity(getAllActiveSuspensions(), HttpStatus.OK)
    }
}

@RestController
@RequestMapping("/api/mySuspension")
class MySuspensionController {

    @Autowired
    private lateinit var tokenService: TokenService

    @Autowired
    private lateinit var userService: UserService

    @GetMapping
    fun getMySuspension(
        @RequestHeader("Authorization") authHeader: String
    ): ResponseEntity<Suspension?> {
        val token = authHeader.removePrefix("Bearer ").trim()
        val username = tokenService.getUsernameFromToken(token)
        val userId = userService.findUserByUsername(username)?.id
            ?: return ResponseEntity(null, HttpStatus.UNAUTHORIZED)

        val suspension = getActiveSuspensionForUser(userId)
        return ResponseEntity(suspension, HttpStatus.OK)
    }
}
