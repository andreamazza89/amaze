package com.andreamazzarella.amaze.core

import com.andreamazzarella.amaze.persistence.MazeRepository
import com.andreamazzarella.amaze.utils.Result
import com.andreamazzarella.amaze.utils.andThen
import com.andreamazzarella.amaze.utils.mapError

class TakeAStep(private val mazeRepository: MazeRepository = MazeRepository()) {

    fun doIt(mazeId: MazeId, stepDirection: StepDirection): Result<Position, StepError> {
        return findAMaze(mazeRepository, mazeId)
            .andThen { maze -> takeAStep(maze, stepDirection) }
            .andThen { newPosition -> updatePosition(mazeRepository, mazeId, newPosition) }
    }
}

private fun findAMaze(mazeRepository: MazeRepository, mazeId: MazeId): Result<Maze, StepError> =
    mazeRepository.findAMaze(mazeId).mapError { StepError(MazeNotFound) }

private fun takeAStep(maze: Maze, stepDirection: StepDirection): Result<Position, StepError> =
    maze.takeAStep(stepDirection).mapError { StepError(HitAWall) }

private fun updatePosition(
    mazeRepository: MazeRepository,
    mazeId: MazeId,
    newPosition: Position
): Result<Position, StepError> =
    mazeRepository.updatePosition(mazeId, newPosition).mapError { StepError(MazeNotFound) }

data class StepError(val error: PotentialStepError)
sealed class PotentialStepError
object MazeNotFound : PotentialStepError()
object HitAWall : PotentialStepError()
