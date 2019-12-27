package com.andreamazzarella.amaze.core.usecases

import com.andreamazzarella.amaze.core.GameId
import com.andreamazzarella.amaze.core.MazeId
import com.andreamazzarella.amaze.core.aMazeFromADrawing
import com.andreamazzarella.amaze.persistence.GameRepository
import com.andreamazzarella.amaze.persistence.MazeRepository
import com.andreamazzarella.amaze.utils.Err
import com.andreamazzarella.amaze.utils.Ok
import com.andreamazzarella.amaze.utils.Result
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

    fun doIt2(
        gameId: GameId,
        mazeDrawing: String = defaultMaze()
    ): Result<MazeId, GameDoesNotExist> {
        return when (GameRepository.find(gameId)) {
            is Ok -> Ok(UUID.randomUUID())
            is Err -> Err(GameDoesNotExist)
        }
    }

    private fun defaultMaze() =
        """
            ⬛⚪⬛⬛⬛
            ⬛⬜⬜⬜⬤
            ⬛⬛⬛⬛⬛
        """.trimIndent()
}

object GameDoesNotExist
