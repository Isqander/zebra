package org.example.zebra.controller

import org.example.zebra.dto.House
import org.example.zebra.dto.PuzzleRequest
import org.example.zebra.service.PuzzleService
import org.springframework.web.bind.annotation.*
import org.springframework.http.ResponseEntity

@RestController
@RequestMapping("/puzzle")
class PuzzleController(private val puzzleService: PuzzleService) {

    @PostMapping("/solve")
    fun solvePuzzle(@RequestBody puzzleRequest: PuzzleRequest): ResponseEntity<List<List<House>>> {
        val response = puzzleService.solveZebraPuzzle(puzzleRequest)

        return ResponseEntity.ok(response)
    }
}