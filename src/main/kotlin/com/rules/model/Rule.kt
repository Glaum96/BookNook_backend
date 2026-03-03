package com.rules.model

data class Rule(
    val id: String,
    val name: String,
    val description: String,
    val enabled: Boolean,
    val value: Int
)
