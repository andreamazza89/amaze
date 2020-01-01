package com.andreamazzarella.amaze.core

import com.andreamazzarella.amaze.core.Position.*
import java.lang.RuntimeException
import java.util.UUID

const val DEFAULT_MAZE = """
                            ⬛⚪⬛⬛
                            ⬛⬜⬜⬤
                            ⬛⬛⬛⬛
                         """

fun aMazeFromADrawing(
    drawing: String,
    entrance: Position = Position(Row(0), Column(1)),
    id: UUID = UUID.randomUUID()
): Maze {
    val rows = drawing.trimIndent().split("\n").map { row -> row.toList() }
    val cells = gatherCells(rows)
    val currentPosition = findCurrentPosition(rows)
    val exit = findExit(rows)
    return Maze(
        id = id,
        cells = cells,
        currentPosition = currentPosition,
        entrance = entrance,
        exit = exit
    )
}

private fun findExit(rows: List<List<Char>>) = findPositionOf('⬤', rows)

private fun findCurrentPosition(rows: List<List<Char>>) = findPositionOf('⚪', rows)

private fun findPositionOf(character: Char, rows: List<List<Char>>): Position {
    val accumulator: Position? = null
    return rows.foldIndexed(accumulator, { rowIndex, currentPosition, row ->
        if (row.indexOf(character) != -1) {
            Position(Row(rowIndex), Column(row.indexOf(character)))
        } else {
            currentPosition
        }
    })!!
}

private fun gatherCells(rows: List<List<Char>>): List<Cell> {
    return rows.mapIndexed { rowIndex, row ->
        row.mapIndexed { columnIndex, cellAsCharacter ->
            when (cellAsCharacter) {
                '⬛' -> Wall(Position(Row(rowIndex), Column(columnIndex)))
                '⚪', '⬜', '⬤' -> Floor(Position(Row(rowIndex), Column(columnIndex)))
                else -> throw RuntimeException("this should never happen")
            }
        }
    }.flatten()
}

