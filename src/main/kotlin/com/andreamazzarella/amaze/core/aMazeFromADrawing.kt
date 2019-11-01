package com.andreamazzarella.amaze.core

import java.lang.RuntimeException
import java.util.UUID

fun aMazeFromADrawing(drawing: String, id: UUID = UUID.randomUUID()): Maze {
    val rows = drawing.split("\n").map { row -> row.toList() }
    val cells = gatherCells(rows)
    val currentPosition = findCurrentPosition(rows)
    val exit = findExit(rows)
    return Maze(
        id = id,
        cells = cells,
        currentPosition = currentPosition,
        exit = exit
    )
}

private fun findExit(rows: List<List<Char>>) = findPositionOf('⬤', rows)

private fun findCurrentPosition(rows: List<List<Char>>) = findPositionOf('⚪', rows)

private fun findPositionOf(character: Char, rows: List<List<Char>>): Position {
    val accumulator: Position? = null
    return rows.foldIndexed(accumulator, { rowIndex, currentPosition, row ->
        if (row.indexOf(character) != -1) {
            Position(Position.Row(rowIndex), Position.Column(row.indexOf(character)))
        } else {
            currentPosition
        }
    })!!
}

private fun gatherCells(rows: List<List<Char>>): List<Cell> {
    return rows.mapIndexed { rowIndex, row ->
        row.mapIndexed { columnIndex, cellAsCharacter ->
            when (cellAsCharacter) {
                '⬛' -> Wall(Position(Position.Row(rowIndex), Position.Column(columnIndex)))
                '⚪', '⬜', '⬤' -> Floor(Position(Position.Row(rowIndex), Position.Column(columnIndex)))
                else -> throw RuntimeException("this should never happen")
            }
        }
    }.flatten()
}

