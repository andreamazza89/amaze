package com.andreamazzarella.amaze.persistence

import com.andreamazzarella.amaze.core.Maze
import com.andreamazzarella.amaze.core.MazeId
import com.andreamazzarella.amaze.core.Position
import com.andreamazzarella.amaze.core.createAMaze
import com.andreamazzarella.amaze.utils.Err
import com.andreamazzarella.amaze.utils.Ok
import com.andreamazzarella.amaze.utils.Result
import java.util.UUID

class MazeRepository {
    private val mazes: MutableMap<MazeId, Maze> = mutableMapOf()

    fun findAMaze(mazeId: MazeId): Result<Maze, MazeNotFoundError> {
        return Ok(createAMaze())
    }

    fun findById(mazeId: MazeId): Result<Maze, String> {
        return Ok(mazes[mazeId]!!)
    }

    fun updatePosition(mazeId: MazeId, newPosition: Position): Result<Position, MazeNotFoundError> {
        val mazeToUpdate = mazes[mazeId]
        return if (mazeToUpdate == null) {
            Err(MazeNotFoundError())
        } else {
            mazes[mazeId] = mazes[mazeId]!!.withPosition(newPosition)
            Ok(newPosition)
        }
    }

    fun createOne(): MazeId {
        val mazeId = UUID.randomUUID()
        mazes[mazeId] = createAMaze(mazeId)
        return mazeId
    }
}

data class MazeNotFoundError(val message: String = "maze was not found")