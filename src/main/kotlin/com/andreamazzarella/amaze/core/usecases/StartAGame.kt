package com.andreamazzarella.amaze.core.usecases

import com.andreamazzarella.amaze.core.Game
import com.andreamazzarella.amaze.core.GameId
import com.andreamazzarella.amaze.core.Maze
import com.andreamazzarella.amaze.core.mediumMaze
import com.andreamazzarella.amaze.persistence.GameRepository

object StartAGame {
    fun doIt(maze: Maze =  mediumMaze): GameId =
        GameRepository.save(Game(maze = maze))
}