package com.andreamazzarella.amaze.core

import com.andreamazzarella.amaze.persistence.MazeRepository
import java.util.UUID

class MakeAMaze(private val mazeRepository: MazeRepository = MazeRepository()) {
    fun doIt(mazeDrawing: String): MazeId {
        val mazeId = UUID.randomUUID()
        mazeRepository.save(mazeId, aMazeFromADrawing(mazeId, mazeDrawing))
        return mazeId
    }
}
