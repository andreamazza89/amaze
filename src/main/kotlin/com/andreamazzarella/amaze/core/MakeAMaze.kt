package com.andreamazzarella.amaze.core

import com.andreamazzarella.amaze.persistence.MazeRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class MakeAMaze(@Autowired private val mazeRepository: MazeRepository) {
    fun doIt(mazeDrawing: String = defaultMaze()): MazeId {
        val mazeId = UUID.randomUUID()
        mazeRepository.save(mazeId, aMazeFromADrawing(mazeDrawing, mazeId))
        return mazeId
    }

    private fun defaultMaze() =
        """
            ⬛⚪⬛⬛⬛
            ⬛⬜⬜⬜⬤
            ⬛⬛⬛⬛⬛
        """.trimIndent()
}
