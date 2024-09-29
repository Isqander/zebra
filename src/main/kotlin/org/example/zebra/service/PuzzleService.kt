package org.example.zebra.service


import org.example.zebra.dto.House
import org.example.zebra.dto.PuzzleRequest
import org.springframework.stereotype.Service

@Service
class PuzzleService(private val solver: Solver) {

    fun solveZebraPuzzle(puzzleRequest: PuzzleRequest): List<List<House>> {
        puzzleRequest.validate()
        return solver.solvePuzzle(puzzleRequest)
    }
}

fun PuzzleRequest.validate() {
    require(constraints.all { it.attributes.all { atr -> entities[atr.type]?.contains(atr.name) == true } }) {
        "Unexpected attribute in constraints"
    }
    require(housesQuantity > 0) { "Houses quantity should be greater than 0" }
    require(entities.all { it.value.size == housesQuantity }) {
        "Number of entities of each type should be equal to houses number"
    }
}