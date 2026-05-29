package com.checkin

import com.audit.AuditService
import com.audit.model.AuditAction
import com.checkin.model.*
import com.users.UserUtil
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("/api/bookings")
class CheckinController(private val userUtil: UserUtil, private val auditService: AuditService) {

    @PostMapping("/{bookingId}/checkin", consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    fun uploadCheckin(
        @PathVariable bookingId: String,
        @RequestParam("file") file: MultipartFile,
        @RequestHeader("Authorization") authHeader: String,
        @RequestHeader("User-Id") userId: String
    ): ResponseEntity<Map<String, Any>> {
        if (!userUtil.requestingUserIsSameAsTargetUser(userId, authHeader) &&
            !userUtil.validateAdminAction(authHeader)) {
            return ResponseEntity(mapOf("error" to "Ingen tilgang"), HttpStatus.FORBIDDEN)
        }
        return uploadImage(bookingId, userId, "CHECK_IN", file, authHeader)
    }

    @PostMapping("/{bookingId}/checkout", consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    fun uploadCheckout(
        @PathVariable bookingId: String,
        @RequestParam("file") file: MultipartFile,
        @RequestHeader("Authorization") authHeader: String,
        @RequestHeader("User-Id") userId: String
    ): ResponseEntity<Map<String, Any>> {
        if (!userUtil.requestingUserIsSameAsTargetUser(userId, authHeader) &&
            !userUtil.validateAdminAction(authHeader)) {
            return ResponseEntity(mapOf("error" to "Ingen tilgang"), HttpStatus.FORBIDDEN)
        }
        return uploadImage(bookingId, userId, "CHECK_OUT", file, authHeader)
    }

    private fun uploadImage(
        bookingId: String,
        userId: String,
        type: String,
        file: MultipartFile,
        authHeader: String
    ): ResponseEntity<Map<String, Any>> {
        return try {
            val fileId = uploadCheckinImage(
                bookingId = bookingId,
                userId = userId,
                type = type,
                filename = file.originalFilename ?: "image",
                contentType = file.contentType ?: "application/octet-stream",
                inputStream = file.inputStream
            )
            val action = if (type == "CHECK_IN") AuditAction.CHECKIN_IMAGE_UPLOADED else AuditAction.CHECKOUT_IMAGE_UPLOADED
            val label = if (type == "CHECK_IN") "Check-in bilde lastet opp" else "Check-out bilde lastet opp"
            auditService.log(authHeader, action, bookingId, label)
            ResponseEntity(mapOf("success" to true, "imageId" to fileId), HttpStatus.CREATED)
        } catch (e: CheckinTimeWindowException) {
            ResponseEntity(mapOf("error" to (e.message ?: "Ugyldig tidspunkt")), HttpStatus.BAD_REQUEST)
        } catch (e: IllegalArgumentException) {
            ResponseEntity(mapOf("error" to (e.message ?: "Ugyldig forespørsel")), HttpStatus.BAD_REQUEST)
        }
    }

    @GetMapping("/checkin-summary")
    fun getCheckinSummary(
        @RequestHeader("Authorization") authHeader: String
    ): ResponseEntity<List<BookingCheckinSummary>> {
        if (!userUtil.validateAdminAction(authHeader)) {
            return ResponseEntity(HttpStatus.FORBIDDEN)
        }
        return ResponseEntity.ok(getCheckinSummaryForRecentBookings())
    }

    @GetMapping("/{bookingId}/images")
    fun getImages(
        @PathVariable bookingId: String,
        @RequestHeader("Authorization") authHeader: String
    ): ResponseEntity<List<CheckinImage>> {
        return ResponseEntity.ok(getCheckinImages(bookingId))
    }
}

@RestController
@RequestMapping("/api/images")
class ImageDownloadController(private val userUtil: UserUtil, private val auditService: AuditService) {

    @GetMapping("/{imageId}")
    fun getImage(
        @PathVariable imageId: String,
        @RequestHeader("Authorization") authHeader: String,
        response: HttpServletResponse
    ) {
        val metadata = getCheckinImageMetadata(imageId)
            ?: run {
                response.status = HttpStatus.NOT_FOUND.value()
                return
            }

        val isAdmin = userUtil.validateAdminAction(authHeader)
        val isSelf = userUtil.requestingUserIsSameAsTargetUser(metadata.userId, authHeader)
        if (!isAdmin && !isSelf) {
            response.status = HttpStatus.FORBIDDEN.value()
            return
        }

        response.contentType = metadata.contentType.ifBlank { "image/jpeg" }
        response.setHeader("Content-Disposition", "inline; filename=\"${metadata.filename}\"")
        streamCheckinImage(imageId, response.outputStream)
    }

    @DeleteMapping("/{imageId}")
    fun deleteImage(
        @PathVariable imageId: String,
        @RequestHeader("Authorization") authHeader: String
    ): ResponseEntity<Map<String, Any>> {
        val metadata = getCheckinImageMetadata(imageId)
            ?: return ResponseEntity(mapOf("error" to "Bildet finnes ikke"), HttpStatus.NOT_FOUND)

        val isAdmin = userUtil.validateAdminAction(authHeader)
        val isOwner = userUtil.requestingUserIsSameAsTargetUser(metadata.userId, authHeader)
        if (!isAdmin && !isOwner) {
            return ResponseEntity(mapOf("error" to "Ingen tilgang"), HttpStatus.FORBIDDEN)
        }
        deleteCheckinImage(imageId)
        auditService.log(authHeader, AuditAction.CHECKIN_IMAGE_DELETED, imageId, "Check-in/out bilde slettet")
        return ResponseEntity.ok(mapOf("success" to true))
    }
}
