package com.andreamazzarella.amaze.core

import com.andreamazzarella.amaze.utils.pipe
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.springframework.util.ResourceUtils
import java.nio.file.Files
import java.util.UUID

val MAZE_SOURCE: String = ResourceUtils.getFile("classpath:bigMaze.json").pipe { Files.readString(it.toPath())}
const val MAZE_SIZE_LENGTH = 81
val ENTRANCE = Position(Position.Row(0), Position.Column(1))
val EXIT = Position(Position.Row(81), Position.Column(80))

val bigMaze: Maze by lazy {
    ObjectMapper().readValue<List<Int>>(MAZE_SOURCE)
        .chunked(MAZE_SIZE_LENGTH)
        .mapIndexed { rowIndex, row ->
            row.mapIndexed { columnIndex, cell -> parseCell(cell, rowIndex, columnIndex) }
        }.flatten()
        .pipe { Maze(id = UUID.randomUUID(), cells = it, entrance = ENTRANCE, exit = EXIT) }
}

fun parseCell(cellType: Int, rowIndex: Int, columnIndex: Int): Cell {
    val position = Position(Position.Row(rowIndex), Position.Column(columnIndex))
    return when (cellType) {
        0 -> Cell.Wall(position)
        else -> Cell.Floor(position)
    }
}
