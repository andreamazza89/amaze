package com.andreamazzarella.amaze.web


import com.andreamazzarella.amaze.core.Cell.Floor
import com.andreamazzarella.amaze.core.Cell.OutsideMaze
import com.andreamazzarella.amaze.core.Cell.Wall
import com.andreamazzarella.amaze.core.Game
import com.andreamazzarella.amaze.core.Maze
import com.andreamazzarella.amaze.core.Position
import com.andreamazzarella.amaze.core.StepDirection
import com.andreamazzarella.amaze.core.usecases.AddAPlayerError
import com.andreamazzarella.amaze.core.usecases.DirectionsAvailableError
import com.andreamazzarella.amaze.core.usecases.TakeAStepError
import com.andreamazzarella.amaze.utils.Err
import com.andreamazzarella.amaze.utils.Ok
import com.andreamazzarella.amaze.utils.Result
import java.util.UUID

object Mappers {
    // Domain --> Response

    fun toGameStatusResponse(game: Game) =
        GameStatusResponse(
            MazeResponse(toCellsResponse(game.maze)),
            game.playersPositions().map(::toPlayerPositionResponse)
        )

    fun toStatusResponse(result: Result<Position.Status, DirectionsAvailableError>): PlayerStatusResponse =
        when (result) {
            is Ok ->
                when (result.okValue) {
                    is Position.Status.OutsideTheMaze ->
                        PlayerStatusResponse.GotOut("You are out of the maze!!")
                    is Position.Status.InsideTheMaze ->
                        PlayerStatusResponse.StillIn(result.okValue.directionsAvailable.map(::toStepDirectionResponse))
                }
            is Err ->
                PlayerStatusResponse.Failure("Something went wrong: maybe the gameID or playerName is invalid?")
        }

    fun toAddAPlayerResponse(result: Result<String, AddAPlayerError>, playerToken: UUID): AddAPlayerResponse =
        when (result) {
            is Ok -> AddAPlayerResponse.Success(playerToken)
            is Err -> AddAPlayerResponse.FailedToAdd("could not add player")
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

    private fun toPlayerPositionResponse(player: Triple<String, Position, Boolean>) =
        PlayerInfoResponse(player.first, toPositionResponse(player.second), player.third)

    private fun toPositionResponse(position: Position) =
        PositionResponse(position.x(), position.y())

    private fun toStepResultErrorResponse(error: TakeAStepError): StepResultResponse =
        when (error) {
            TakeAStepError.GameDoesNotExist -> StepResultResponse.GameDoesNotExist("could not find your maze")
            TakeAStepError.InvalidStep -> StepResultResponse.HitAWall("you hit a wall")
            TakeAStepError.PlayerNotInThisGame -> StepResultResponse.PlayerNotInThisGame("this player does not exist in this game")
            TakeAStepError.TokenIsNotValid -> StepResultResponse.TokenIsNotValid("The token you provided is not valid")
            TakeAStepError.PlayerGotOut -> StepResultResponse.PlayerGotOut("This player is already out of the Maze")
        }

    private fun toStepDirectionResponse(stepDirection: StepDirection): StepDirectionResponse =
        when (stepDirection) {
            StepDirection.UP -> StepDirectionResponse.NORTH
            StepDirection.RIGHT -> StepDirectionResponse.EAST
            StepDirection.DOWN -> StepDirectionResponse.SOUTH
            StepDirection.LEFT -> StepDirectionResponse.WEST
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