package com.andreamazzarella.amaze.core

import com.andreamazzarella.amaze.persistence.MazeRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class GetAMaze(@Autowired private val mazeRepository: MazeRepository) {
    fun doIt(mazeId: MazeId) = mazeRepository.findAMaze(mazeId)
}
