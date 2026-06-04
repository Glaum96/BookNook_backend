package com.blockeddates

import com.blockeddates.model.BlockedDate
import com.blockeddates.model.addBlockedDateToDB
import com.blockeddates.model.deleteBlockedDateFromDB
import com.blockeddates.model.getBlockedDatesFromDB
import com.users.UserUtil
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/blockedDates")
class BlockedDatesController {

    @Autowired
    private lateinit var userUtil: UserUtil

    @GetMapping
    fun getBlockedDates(): List<BlockedDate> {
        return getBlockedDatesFromDB()
    }

    data class AddBlockedDateRequest(val date: String, val label: String? = null)

    @PostMapping
    fun addBlockedDate(
        @RequestBody body: AddBlockedDateRequest,
        @RequestHeader("Authorization") authorizationHeader: String
    ): ResponseEntity<Any> {
        if (!userUtil.validateAdminAction(authorizationHeader)) {
            return ResponseEntity(mapOf("success" to false, "message" to "Forbidden"), HttpStatus.FORBIDDEN)
        }
        val created = addBlockedDateToDB(body.date, body.label)
        return ResponseEntity(created, HttpStatus.CREATED)
    }

    @DeleteMapping("/{id}")
    fun deleteBlockedDate(
        @PathVariable id: String,
        @RequestHeader("Authorization") authorizationHeader: String
    ): ResponseEntity<Map<String, Any>> {
        if (!userUtil.validateAdminAction(authorizationHeader)) {
            return ResponseEntity(mapOf("success" to false, "message" to "Forbidden"), HttpStatus.FORBIDDEN)
        }
        val deleted = deleteBlockedDateFromDB(id)
        return if (deleted) {
            ResponseEntity(mapOf("success" to true), HttpStatus.OK)
        } else {
            ResponseEntity(mapOf("success" to false, "message" to "Not found"), HttpStatus.NOT_FOUND)
        }
    }
}
