package com.andreamazzarella.amaze.core

import com.andreamazzarella.amaze.persistence.MazeRepository

class GetAMaze(private val mazeRepository: MazeRepository = MazeRepository()) {
    fun doIt(mazeId: MazeId) = mazeRepository.findAMaze(mazeId)
}
