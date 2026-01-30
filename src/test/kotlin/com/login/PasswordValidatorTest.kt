package com.login

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*

class PasswordValidatorTest {

    private val validator = PasswordValidator(
        minLength = 8,
        maxLength = 128,
        requireUppercase = true,
        requireLowercase = true,
        requireDigit = true,
        requireSpecial = true
    )

    @Test
    fun `valid password passes all checks`() {
        val result = validator.validate("SecurePass123!")
        assertTrue(result.isValid)
        assertTrue(result.errors.isEmpty())
    }

    @Test
    fun `empty password returns error`() {
        val result = validator.validate("")
        assertFalse(result.isValid)
        assertTrue(result.errors.contains("Password cannot be empty"))
    }

    @Test
    fun `blank password returns error`() {
        val result = validator.validate("   ")
        assertFalse(result.isValid)
        assertTrue(result.errors.contains("Password cannot be empty"))
    }

    @Test
    fun `short password returns length error`() {
        val result = validator.validate("Abc1!")
        assertFalse(result.isValid)
        assertTrue(result.errors.any { it.contains("at least 8 characters") })
    }

    @Test
    fun `password without uppercase returns error`() {
        val result = validator.validate("lowercase123!")
        assertFalse(result.isValid)
        assertTrue(result.errors.any { it.contains("uppercase") })
    }

    @Test
    fun `password without lowercase returns error`() {
        val result = validator.validate("UPPERCASE123!")
        assertFalse(result.isValid)
        assertTrue(result.errors.any { it.contains("lowercase") })
    }

    @Test
    fun `password without digit returns error`() {
        val result = validator.validate("NoDigitsHere!")
        assertFalse(result.isValid)
        assertTrue(result.errors.any { it.contains("number") })
    }

    @Test
    fun `password without special character returns error`() {
        val result = validator.validate("NoSpecial123")
        assertFalse(result.isValid)
        assertTrue(result.errors.any { it.contains("special character") })
    }

    @Test
    fun `multiple failures return multiple errors`() {
        val result = validator.validate("weak")
        assertFalse(result.isValid)
        assertTrue(result.errors.size > 1)
    }

    @Test
    fun `password at minimum length is valid`() {
        val result = validator.validate("Abcdef1!")
        assertTrue(result.isValid)
    }

    @Test
    fun `various special characters are accepted`() {
        val specialChars = listOf("!", "@", "#", "$", "%", "^", "&", "*", "(", ")", "-", "_")
        for (special in specialChars) {
            val result = validator.validate("Password1$special")
            assertTrue(result.isValid, "Should accept special character: $special")
        }
    }
}
