package com.andreamazzarella.amaze.core

import com.andreamazzarella.amaze.persistence.MazeRepository

class MakeAMaze(private val mazeRepository: MazeRepository = MazeRepository()) {
    fun doIt(): MazeId = mazeRepository.createOne()
}
