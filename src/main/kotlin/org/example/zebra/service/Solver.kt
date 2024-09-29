package org.example.zebra.service

import org.example.zebra.dto.House
import org.example.zebra.dto.PuzzleRequest

interface Solver {
    fun solvePuzzle(puzzleRequest: PuzzleRequest): List<List<House>>
}