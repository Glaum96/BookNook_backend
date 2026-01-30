package com.login.model

data class ValidationResult(
    val isValid: Boolean,
    val errors: List<String> = emptyList()
) {
    companion object {
        fun success() = ValidationResult(isValid = true)
        fun failure(errors: List<String>) = ValidationResult(isValid = false, errors = errors)
        fun failure(error: String) = ValidationResult(isValid = false, errors = listOf(error))
    }
}
