package com.andreamazzarella.amaze.core

import com.andreamazzarella.amaze.utils.pipe
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.springframework.core.io.ClassPathResource
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.UUID
import java.util.stream.Collectors

val MAZE_SOURCE = readFileFromClasspath("mediumMaze.json")

const val MAZE_SIZE_LENGTH = 41
val ENTRANCE = Position(Position.Row(0), Position.Column(1))
val EXIT = Position(Position.Row(40), Position.Column(39))

val mediumMaze: Maze by lazy {
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

// this is gross - don't look at this
private fun readFileFromClasspath(fileName: String): String =
    ClassPathResource(fileName)
        .inputStream
        .pipe { InputStreamReader(it) }
        .pipe { BufferedReader(it) }.lines()
        .collect(Collectors.joining("\n"))

