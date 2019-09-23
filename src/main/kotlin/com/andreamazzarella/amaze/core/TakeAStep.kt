package com.andreamazzarella.amaze.core

import com.andreamazzarella.amaze.persistence.MazeRepository
import com.andreamazzarella.amaze.utils.Result

class TakeAStep(private val mazeRepository: MazeRepository = MazeRepository()) {
    fun doIt(mazeId: MazeId, stepDirection: StepDirection): Result<Position, StepError> =
        mazeRepository.findAMaze(mazeId)
            .andThen { maze -> maze.takeAStep(stepDirection) }
            .andThen { newPosition -> mazeRepository.updatePosition(mazeId, newPosition)}
}

sealed class StepError {
    object MazeNotFound : StepError()
    object HitAWall : StepError()
}
