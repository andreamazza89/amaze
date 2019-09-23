package com.andreamazzarella.amaze.core

import com.andreamazzarella.amaze.persistence.MazeRepository
import com.andreamazzarella.amaze.utils.Result

class GetAMaze(private val mazeRepository: MazeRepository = MazeRepository()) {
    fun doIt(mazeId: MazeId) = mazeRepository.findById(mazeId)
}
