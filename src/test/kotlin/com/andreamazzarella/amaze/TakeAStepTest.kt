package com.andreamazzarella.amaze

import com.andreamazzarella.amaze.core.Column
import com.andreamazzarella.amaze.core.GetAMaze
import com.andreamazzarella.amaze.core.MakeAMaze
import com.andreamazzarella.amaze.core.Maze
import com.andreamazzarella.amaze.core.Position
import com.andreamazzarella.amaze.core.Row
import com.andreamazzarella.amaze.core.StepDirection
import com.andreamazzarella.amaze.core.TakeAStep
import com.andreamazzarella.amaze.persistence.MazeRepository
import com.andreamazzarella.amaze.utils.Result
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class TakeAStepTest {
    private val mazeRepository = MazeRepository()
    private val makeAMaze = MakeAMaze(mazeRepository)
    private val getAMaze = GetAMaze(mazeRepository)
    private val takeAStep = TakeAStep(mazeRepository)

    @Test
    fun `taking a valid step reveals the maze runner's new position`() {
        val mazeId = makeAMaze.doIt()

        val stepResult = takeAStep.doIt(mazeId, StepDirection.RIGHT)

        assertEquals(Result.Ok<Position, String>(Position(Row(1), Column(2))), stepResult)
    }

    @Test
    fun `the runner's new position is updated in the Maze after a valid move`() {
        val mazeId = makeAMaze.doIt()

        takeAStep.doIt(mazeId, StepDirection.UP)

        val updatedMaze = getAMaze.doIt(mazeId)
        val expectedPosition = Position(Row(0), Column(1))
        assertEquals(Result.Ok<Maze, String>(Maze(mazeId, expectedPosition)), updatedMaze)
    }
}

