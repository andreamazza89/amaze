package com.andreamazzarella.amaze.core.usecases

import com.andreamazzarella.amaze.core.Maze
import com.andreamazzarella.amaze.core.MazeId
import com.andreamazzarella.amaze.persistence.GameRepository
import com.andreamazzarella.amaze.persistence.MazeNotFoundError
import com.andreamazzarella.amaze.persistence.MazeRepository
import com.andreamazzarella.amaze.utils.Ok
import com.andreamazzarella.amaze.utils.okOrFail
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class GetAMaze(@Autowired private val mazeRepository: MazeRepository) {
    fun doIt(mazeId: MazeId) = mazeRepository.findAMaze(mazeId)

    fun doIt2(mazeId: MazeId) =
        Ok<Maze, MazeNotFoundError>(GameRepository.findGameWithMaze(mazeId).okOrFail().mazes.find { maze -> maze.id == mazeId }!!)
}
