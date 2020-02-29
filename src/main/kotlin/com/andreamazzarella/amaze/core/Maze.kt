package com.andreamazzarella.amaze.core

import com.andreamazzarella.amaze.utils.Err
import com.andreamazzarella.amaze.utils.Ok
import com.andreamazzarella.amaze.utils.Result
import java.util.UUID

typealias MazeId = UUID

data class Maze(
    val id: MazeId,
    val cells: List<Cell>,
    val entrance: Position,
    private val exit: Position
) {
    fun takeAStep(currentPosition: Position, direction: StepDirection): Result<Position, StepError> {
        val newPosition = currentPosition.nearby(direction)
        return when (cellAt(newPosition)) {
            is Wall -> Err(StepError())
            is Floor -> Ok(newPosition)
            is OutsideMaze -> Err(StepError("you walked out of the maze"))
        }
    }

    private fun cellAt(position: Position): Cell = this.cells.find { it.position == position } ?: OutsideMaze(position)
}

sealed class Cell {
    abstract val position: Position
}
data class Floor(override val position: Position) : Cell()
data class Wall(override val position: Position) : Cell()
data class OutsideMaze(override val position: Position) : Cell()

data class StepError(val message: String = "you hit a wall")

enum class StepDirection { UP, RIGHT, DOWN, LEFT }

data class Position(val row: Row, val column: Column) {

    fun x(): Int = this.column.columnNumber
    fun y(): Int = this.row.rowNumber

    fun nearby(direction: StepDirection): Position =
        when (direction) {
            StepDirection.UP -> this.copy(row = this.row.aboveIt())
            StepDirection.RIGHT -> this.copy(column = this.column.toItsRight())
            StepDirection.DOWN -> this.copy(row = this.row.belowIt())
            StepDirection.LEFT -> this.copy(column = this.column.toItsLeft())
        }

    data class Row(val rowNumber: Int) {
        fun aboveIt() = Row(this.rowNumber - 1)
        fun belowIt() = Row(this.rowNumber + 1)
    }

    data class Column(val columnNumber: Int) {
        fun toItsRight() = Column(this.columnNumber + 1)
        fun toItsLeft() = Column(this.columnNumber - 1)
    }
}
