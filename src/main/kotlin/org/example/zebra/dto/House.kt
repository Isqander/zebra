package org.example.zebra.dto

data class House(
    val attributesSet: MutableSet<Attribute> = LinkedHashSet(),
)