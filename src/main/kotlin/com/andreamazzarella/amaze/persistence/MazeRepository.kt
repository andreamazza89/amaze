package com.andreamazzarella.amaze.persistence

import com.andreamazzarella.amaze.core.Maze
import com.andreamazzarella.amaze.core.MazeId
import com.andreamazzarella.amaze.core.Position
import com.andreamazzarella.amaze.utils.Err
import com.andreamazzarella.amaze.utils.Ok
import com.andreamazzarella.amaze.utils.Result
import org.springframework.stereotype.Repository

@Repository
class MazeRepository {
    private val mazes: MutableMap<MazeId, Maze> = mutableMapOf()

    fun save(mazeId: MazeId, maze: Maze) {
        mazes[mazeId] = maze
    }

    fun findAMaze(mazeId: MazeId): Result<Maze, MazeNotFoundError> {
        return if (mazes[mazeId] != null) {
            Ok(mazes[mazeId]!!)
        } else {
            Err(MazeNotFoundError())
        }
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
}

data class MazeNotFoundError(val message: String = "maze was not found")