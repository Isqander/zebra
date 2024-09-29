package org.example.zebra

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import org.example.zebra.dto.PuzzleRequest
import org.example.zebra.service.PuzzleService
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.io.File
import kotlin.test.assertEquals

@SpringBootTest
class ZebraApplicationTests {

    @Autowired
    lateinit var puzzleService: PuzzleService

    private val mapper = ObjectMapper().registerKotlinModule()

    @Test
    fun testStandardPuzzle() {
        val readValue = mapper.readValue(
            File("src/test/resources/static/standardInput.json").readText(),
            PuzzleRequest::class.java
        )
        val solveZebraPuzzle = puzzleService.solveZebraPuzzle(readValue)
        assertEquals(1, solveZebraPuzzle.size)
        assertEquals(5, solveZebraPuzzle[0].size)
        assertEquals("Japanese",
            solveZebraPuzzle[0].first { it.attributesSet.first { attr -> attr.type == "pet" }.name == "zebra" }
                .attributesSet.first { attr -> attr.type == "nationality" }.name
        )
    }

    @Test
    fun testMultipleAnswers() {
        val readValue = mapper.readValue(
            File("src/test/resources/static/multipleAnswersInput.json").readText(),
            PuzzleRequest::class.java
        )
        val solveZebraPuzzle = puzzleService.solveZebraPuzzle(readValue)
        assertEquals(14, solveZebraPuzzle.size)
        assertEquals(5, solveZebraPuzzle[3].size)
    }

    @Test
    fun testNoAnswers() {
        val readValue = mapper.readValue(
            File("src/test/resources/static/noAnswersInput.json").readText(),
            PuzzleRequest::class.java
        )
        val solveZebraPuzzle = puzzleService.solveZebraPuzzle(readValue)
        assertEquals(0, solveZebraPuzzle.size)
    }


    @Test
    fun testPagination() {
        val readValue = mapper.readValue(
            File("src/test/resources/static/paginationInput.json").readText(),
            PuzzleRequest::class.java
        )
        val solveZebraPuzzle = puzzleService.solveZebraPuzzle(readValue)
        assertEquals(4, solveZebraPuzzle.size)
        assertEquals(5, solveZebraPuzzle[3].size)
    }

    @Test
    fun testWrongAttribute() {
        val readValue = mapper.readValue(
            File("src/test/resources/static/wrongAttributeInput.json").readText(),
            PuzzleRequest::class.java
        )
        assertThrows(
            IllegalArgumentException::class.java,
            { puzzleService.solveZebraPuzzle(readValue) },
            "Unexpected attribute in constraints"
        )
    }

    @Test
    fun testFourHouses() {
        val readValue = mapper.readValue(
            File("src/test/resources/static/fourHousesInput.json").readText(),
            PuzzleRequest::class.java
        )
        val solveZebraPuzzle = puzzleService.solveZebraPuzzle(readValue)
        assertEquals(1, solveZebraPuzzle.size)
        assertEquals(4, solveZebraPuzzle[0].size)
    }

}
