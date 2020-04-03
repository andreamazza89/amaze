package com.andreamazzarella.amaze.core

import com.andreamazzarella.amaze.core.Cell.Floor
import com.andreamazzarella.amaze.core.Cell.OutsideMaze
import com.andreamazzarella.amaze.core.Cell.Wall
import com.andreamazzarella.amaze.core.StepDirection.DOWN
import com.andreamazzarella.amaze.core.StepDirection.LEFT
import com.andreamazzarella.amaze.core.StepDirection.RIGHT
import com.andreamazzarella.amaze.core.StepDirection.UP
import com.andreamazzarella.amaze.core.StepError.HitAWall
import com.andreamazzarella.amaze.utils.Err
import com.andreamazzarella.amaze.utils.Ok
import com.andreamazzarella.amaze.utils.Result
import com.andreamazzarella.amaze.utils.pipe
import java.util.UUID

typealias MazeId = UUID

data class Maze(
    val id: MazeId,
    val cells: List<Cell>,
    val entrance: Position,
    private val exit: Position
) {
    fun positionStatus(currentPosition: Position): Position.Status =
        when {
            (currentPosition == exit) -> Position.Status.OutsideTheMaze
            else -> Position.Status.InsideTheMaze(positionsAvailable(currentPosition))
        }

    private fun positionsAvailable(currentPosition: Position) =
        StepDirection.values()
            .filter { direction -> cellNearby(currentPosition, direction) is Floor }

    fun takeAStep(currentPosition: Position, direction: StepDirection): Result<Position, StepError> {
        if (currentPosition == exit) return Err(StepError.AlreadyGotOut)
        val newPosition = currentPosition.nearby(direction)
        return when (cellAt(newPosition)) {
            is Wall -> Err(HitAWall)
            is Floor -> Ok(newPosition)
            is OutsideMaze -> Err(HitAWall)
        }
    }

    private fun cellNearby(currentPosition: Position, direction: StepDirection): Cell =
        currentPosition
            .nearby(direction)
            .pipe(::cellAt)

    private fun cellAt(position: Position): Cell =
        this.cells.find { it.position == position } ?: OutsideMaze(position)
}

sealed class Cell {
    abstract val position: Position

    data class Floor(override val position: Position) : Cell()
    data class Wall(override val position: Position) : Cell()
    data class OutsideMaze(override val position: Position) : Cell()
}

sealed class StepError {
    object HitAWall : StepError()
    object AlreadyGotOut : StepError()
}

enum class StepDirection { UP, RIGHT, DOWN, LEFT }

data class Position(val row: Row, val column: Column) {

    sealed class Status {
        object OutsideTheMaze : Status()
        data class InsideTheMaze(val directionsAvailable: List<StepDirection>) : Status()
    }

    fun x(): Int = this.column.columnNumber
    fun y(): Int = this.row.rowNumber

    fun nearby(direction: StepDirection): Position =
        when (direction) {
            UP -> this.copy(row = this.row.aboveIt())
            RIGHT -> this.copy(column = this.column.toItsRight())
            DOWN -> this.copy(row = this.row.belowIt())
            LEFT -> this.copy(column = this.column.toItsLeft())
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
