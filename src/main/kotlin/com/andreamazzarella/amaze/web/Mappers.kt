package com.andreamazzarella.amaze.web


import com.andreamazzarella.amaze.core.Floor
import com.andreamazzarella.amaze.core.Game
import com.andreamazzarella.amaze.core.Maze
import com.andreamazzarella.amaze.core.OutsideMaze
import com.andreamazzarella.amaze.core.Position
import com.andreamazzarella.amaze.core.StepDirection
import com.andreamazzarella.amaze.core.Wall
import com.andreamazzarella.amaze.core.usecases.AddAPlayerError
import com.andreamazzarella.amaze.core.usecases.TakeAStepError
import com.andreamazzarella.amaze.utils.Err
import com.andreamazzarella.amaze.utils.Ok
import com.andreamazzarella.amaze.utils.Result
object Mappers {
    // Domain --> Response

    fun toGameStatusResponse(game: Game) =
        GameStatusResponse(
            MazeResponse(toCellsResponse(game.maze)),
            game.playersPositions().map(::toPlayerPositionResponse)
        )

    fun toAddAPlayerResponse(result: Result<String, AddAPlayerError>): AddAPlayerResponse =
        when (result) {
            is Ok -> AddAPlayerResponse.Success("player added")
            is Err -> AddAPlayerResponse.Failure("could not add player")
        }

    fun toTakeAStepResponse(stepResult: Result<Position, TakeAStepError>): StepResultResponse =
        when (stepResult) {
            is Ok -> StepResultResponse.NewPosition(toPositionResponse(stepResult.okValue))
            is Err -> toStepResultErrorResponse(stepResult.errorValue)
        }

    private fun toCellsResponse(maze: Maze): List<CellResponse> =
        maze.cells.map { cell ->
            when (cell) {
                is Wall -> CellResponse.Wall(toPositionResponse(cell.position))
                is Floor -> CellResponse.Floor(toPositionResponse(cell.position))
                is OutsideMaze -> TODO()
            }
        }

    private fun toPlayerPositionResponse(player: Pair<String, Position>) =
        PlayerPositionResponse(player.first, toPositionResponse(player.second))

    private fun toPositionResponse(position: Position) =
        PositionResponse(position.x(), position.y())

    private fun toStepResultErrorResponse(error: TakeAStepError): StepResultResponse =
        when (error) {
            TakeAStepError.GameDoesNotExist -> StepResultResponse.GameDoesNotExist("could not find your maze")
            TakeAStepError.InvalidStep -> StepResultResponse.HitAWall("you hit a wall")
            TakeAStepError.PlayerNotInThisGame -> TODO()
        }

    // Request --> Domain

    fun toStepDirection(stepDirectionRequest: StepDirectionRequest) =
        when (stepDirectionRequest) {
            StepDirectionRequest.NORTH -> StepDirection.UP
            StepDirectionRequest.EAST -> StepDirection.RIGHT
            StepDirectionRequest.SOUTH -> StepDirection.DOWN
            StepDirectionRequest.WEST -> StepDirection.LEFT
        }
}