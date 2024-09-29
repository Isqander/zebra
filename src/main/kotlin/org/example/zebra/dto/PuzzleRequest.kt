package org.example.zebra.dto

data class PuzzleRequest(
    val pageNumber: Int = 1,
    val pageSize: Int = 100,
    val housesQuantity: Int,
    val entities: Map<String, Set<String>>,
    val constraints: Set<Rule>,
)