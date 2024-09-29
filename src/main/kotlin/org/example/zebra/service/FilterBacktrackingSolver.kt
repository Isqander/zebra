package org.example.zebra.service

import org.example.zebra.dto.Attribute
import org.example.zebra.dto.House
import org.example.zebra.dto.Operator.*
import org.example.zebra.dto.PuzzleRequest
import org.example.zebra.dto.Rule
import org.springframework.stereotype.Component
import kotlin.math.abs

@Component
class FilterBacktrackingSolver : Solver {
    override fun solvePuzzle(puzzleRequest: PuzzleRequest): List<List<House>> {
        val solutionsPage = mutableListOf<List<House>>()
        val startIndex = (puzzleRequest.pageNumber - 1) * puzzleRequest.pageSize
        val endIndex = startIndex + puzzleRequest.pageSize
        val validCombinations = getValidCombinations(puzzleRequest)

        backTrackingWithPagination(
            puzzleRequest.constraints,
            List(puzzleRequest.housesQuantity) { House() },
            0,
            validCombinations,
            solutionsPage,
            startIndex,
            endIndex
        )

        return solutionsPage
    }

    fun getValidCombinations(puzzleRequest: PuzzleRequest): List<Set<Attribute>> {
        val groupedAttributes = puzzleRequest.entities.map { it.value.map { ent -> Attribute(it.key, ent) } }.toList()
        val allCombinations = findCombinationsRecursively(groupedAttributes)
        return filterCombinations(puzzleRequest.constraints, allCombinations)
    }

    private fun findCombinationsRecursively(lists: List<List<Attribute>>): List<Set<Attribute>> {
        if (lists.isEmpty()) return listOf(emptySet())
        return lists.first().flatMap { element ->
            findCombinationsRecursively(lists.drop(1)).map { setOf(element) + it }
        }
    }

    private fun filterCombinations(constraints: Set<Rule>, combinations: List<Set<Attribute>>): List<Set<Attribute>> {
        return combinations.filter { combination ->
            constraints.all { it.operator != SAME || combination.satisfies(it) }
        }
    }

    private fun Set<Attribute>.satisfies(rule: Rule): Boolean =
        when {
            this.contains(rule.attributes.first()) && this.contains(rule.attributes.last()) -> true
            !this.contains(rule.attributes.first()) && !this.contains(rule.attributes.last()) -> true
            else -> false
        }

    private fun backTrackingWithPagination(
        rules: Set<Rule>,
        houses: List<House>,
        index: Int = 0,
        possibleCombinations: List<Set<Attribute>>,
        solutionsPage: MutableList<List<House>>,
        startIndex: Int,
        endIndex: Int,
        solutionCount: Int = 0
    ): Int {
        if (solutionsPage.size >= endIndex) return solutionCount

        if (index == houses.size) {
            if (isValidByPuzzleRequest(rules, houses)) {
                if (solutionCount in startIndex..<endIndex) {
                    solutionsPage.add(houses.map { it.copy(attributesSet = it.attributesSet.toMutableSet()) })
                }
                return solutionCount + 1
            }
            return solutionCount
        }

        var currentSolutionCount = solutionCount
        for (combination in possibleCombinations) {
            for (attribute in combination) {
                houses[index].attributesSet.add(attribute)
            }
            val narrowedCombinations = possibleCombinations.filter { it.intersect(combination).isEmpty() }
            currentSolutionCount = backTrackingWithPagination(
                rules,
                houses,
                index + 1,
                narrowedCombinations,
                solutionsPage,
                startIndex,
                endIndex,
                currentSolutionCount
            )
            houses[index].attributesSet.clear()

            if (solutionsPage.size >= endIndex) break
        }
        return currentSolutionCount
    }


    private fun isValidByPuzzleRequest(rules: Set<Rule>, houses: List<House>): Boolean {
        return rules.all { rule ->
            applyRule(rule, houses)
        }
    }

    private fun applyRule(rule: Rule, houses: List<House>): Boolean {
        val firstIndex = houses.indexOfFirst { it.attributesSet.contains(rule.attributes.first()) }
        val secondIndex = houses.indexOfFirst { it.attributesSet.contains(rule.attributes.last()) }

        return when (rule.operator) {
            SAME -> firstIndex != -1 && firstIndex == secondIndex
            NEXT_TO -> firstIndex != -1 && secondIndex != -1 && abs(firstIndex - secondIndex) == 1
            TO_THE_LEFT -> firstIndex != -1 && secondIndex != -1 && firstIndex == secondIndex + 1
            TO_THE_RIGHT -> firstIndex != -1 && secondIndex != -1 && firstIndex == secondIndex - 1
            POSITION -> rule.position?.let { position ->
                houses.getOrNull(position - 1)?.attributesSet?.contains(rule.attributes.first())
            } ?: false
        }
    }

}