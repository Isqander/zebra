package org.example.zebra.dto

data class Rule(
    val attributes: Set<Attribute>,
    val position: Int? = null,
    val operator: Operator
)
