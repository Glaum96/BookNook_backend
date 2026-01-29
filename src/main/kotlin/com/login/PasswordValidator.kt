package com.login

import com.login.model.ValidationResult
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class PasswordValidator(
    @Value("\${password.min-length:8}")
    private val minLength: Int,

    @Value("\${password.max-length:128}")
    private val maxLength: Int,

    @Value("\${password.require-uppercase:true}")
    private val requireUppercase: Boolean,

    @Value("\${password.require-lowercase:true}")
    private val requireLowercase: Boolean,

    @Value("\${password.require-digit:true}")
    private val requireDigit: Boolean,

    @Value("\${password.require-special:true}")
    private val requireSpecial: Boolean
) {

    companion object {
        private val SPECIAL_CHARACTERS = setOf(
            '!', '@', '#', '$', '%', '^', '&', '*', '(', ')', '-', '_',
            '+', '=', '[', ']', '{', '}', '|', '\\', ':', ';', '"', '\'',
            '<', '>', ',', '.', '?', '/'
        )
    }

    fun validate(password: String): ValidationResult {
        val errors = mutableListOf<String>()

        if (password.isBlank()) {
            return ValidationResult.failure("Password cannot be empty")
        }

        if (password.length < minLength) {
            errors.add("Password must be at least $minLength characters long")
        }

        if (password.length > maxLength) {
            errors.add("Password must not exceed $maxLength characters")
        }

        if (requireUppercase && !password.any { it.isUpperCase() }) {
            errors.add("Password must contain at least one uppercase letter")
        }

        if (requireLowercase && !password.any { it.isLowerCase() }) {
            errors.add("Password must contain at least one lowercase letter")
        }

        if (requireDigit && !password.any { it.isDigit() }) {
            errors.add("Password must contain at least one number")
        }

        if (requireSpecial && !password.any { it in SPECIAL_CHARACTERS }) {
            errors.add("Password must contain at least one special character (!@#\$%^&*...)")
        }

        return if (errors.isEmpty()) {
            ValidationResult.success()
        } else {
            ValidationResult.failure(errors)
        }
    }
}
