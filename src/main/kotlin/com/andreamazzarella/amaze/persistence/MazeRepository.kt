package com.andreamazzarella.amaze.persistence

import com.andreamazzarella.amaze.core.Maze
import com.andreamazzarella.amaze.core.MazeId
import com.andreamazzarella.amaze.core.Position
import com.andreamazzarella.amaze.core.StepError
import com.andreamazzarella.amaze.core.createAMaze
import com.andreamazzarella.amaze.utils.Result
import java.util.UUID

class MazeRepository {
    private val mazes: MutableMap<MazeId, Maze> = mutableMapOf()

    fun findAMaze(mazeId: MazeId): Result<Maze, StepError> {
        return Result.Ok(createAMaze())
    }

    fun findById(mazeId: MazeId): Result<Maze, String> {
        return Result.Ok(mazes[mazeId]!!)
    }

    fun updatePosition(mazeId: MazeId, newPosition: Position): Result<Position, StepError> {
        mazes[mazeId] = mazes[mazeId]!!.withPosition(newPosition)
        return Result.Ok(newPosition)
    }

    fun createOne(): MazeId {
        val mazeId = UUID.randomUUID()
        mazes[mazeId] = createAMaze(mazeId)
        return mazeId
    }
}