package com.andreamazzarella.amaze.core.usecases

import com.andreamazzarella.amaze.core.Maze
import com.andreamazzarella.amaze.core.MazeId
import com.andreamazzarella.amaze.core.Position
import com.andreamazzarella.amaze.core.StepDirection
import com.andreamazzarella.amaze.persistence.GameRepository
import com.andreamazzarella.amaze.persistence.MazeRepository
import com.andreamazzarella.amaze.utils.Ok
import com.andreamazzarella.amaze.utils.Result
import com.andreamazzarella.amaze.utils.andThen
import com.andreamazzarella.amaze.utils.mapError
import com.andreamazzarella.amaze.utils.okOrFail
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class TakeAStep(@Autowired private val mazeRepository: MazeRepository) {

    fun doIt(mazeId: MazeId, stepDirection: StepDirection): Result<Position, TakeAStepError> {
        return findAMaze(mazeRepository, mazeId)
            .andThen { maze -> takeAStep(maze, stepDirection) }
            .andThen { newPosition ->
                updatePosition(
                    mazeRepository,
                    mazeId,
                    newPosition
                )
            }
    }

    fun doIt2(mazeId: MazeId, stepDirection: StepDirection): Result<Position, TakeAStepError> {
        val game = GameRepository.findGameWithMaze(mazeId).okOrFail()
        val maze = game.mazes.find { maze -> maze.id == mazeId }!!
        val newMaze = maze.takeAStep2(stepDirection).okOrFail()
        val gameUpdated = game.updateMaze(newMaze)
        GameRepository.updateGame(gameUpdated)
        return Ok(newMaze.currentPosition)
    }
}

private fun findAMaze(mazeRepository: MazeRepository, mazeId: MazeId): Result<Maze, TakeAStepError> =
    mazeRepository.findAMaze(mazeId).mapError { TakeAStepError(MazeNotFound) }

private fun takeAStep(maze: Maze, stepDirection: StepDirection): Result<Position, TakeAStepError> =
    maze.takeAStep(stepDirection).mapError { TakeAStepError(HitAWall) }

private fun updatePosition(
    mazeRepository: MazeRepository,
    mazeId: MazeId,
    newPosition: Position
): Result<Position, TakeAStepError> =
    mazeRepository.updatePosition(mazeId, newPosition).mapError {
        TakeAStepError(
            MazeNotFound
        )
    }

data class TakeAStepError(val error: PotentialStepError)
sealed class PotentialStepError
object MazeNotFound : PotentialStepError()
object HitAWall : PotentialStepError()
