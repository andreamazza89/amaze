package com.andreamazzarella.amaze.core

import com.andreamazzarella.amaze.utils.Ok
import com.andreamazzarella.amaze.utils.Result
import java.util.UUID

typealias MazeId = UUID

data class Maze(
    private val id: MazeId,
    private val cells: List<Cell>,
    val currentPosition: Position,
    private val exit: Position
) {

    fun takeAStep(direction: StepDirection): Result<Position, HitAWallError> {
        val newPosition = this.currentPosition.nearby(direction)
        return Ok(newPosition)
    }

    fun withPosition(newPosition: Position): Maze = this.copy(currentPosition = newPosition)
}

sealed class Cell {
    abstract val position: Position
}
data class Floor(override val position: Position) : Cell()
data class Wall(override val position: Position) : Cell()

class HitAWallError(val message: String = "you hit a wall")

enum class StepDirection { UP, RIGHT, DOWN, LEFT }

data class Position(private val row: Row, private val column: Column) {

    fun nearby(direction: StepDirection): Position =
        when (direction) {
            StepDirection.UP -> this.copy(row = this.row.aboveIt())
            StepDirection.RIGHT -> this.copy(column = this.column.toItsRight())
            StepDirection.DOWN -> this.copy(row = this.row.belowIt())
            StepDirection.LEFT -> TODO()
        }

    data class Row(private val rowNumber: Int) {
        fun aboveIt() = Row(this.rowNumber - 1)
        fun belowIt() = Row(this.rowNumber + 1)
    }

    data class Column(private val columnNumber: Int) {
        fun toItsRight() = Column(this.columnNumber + 1)
    }
}


