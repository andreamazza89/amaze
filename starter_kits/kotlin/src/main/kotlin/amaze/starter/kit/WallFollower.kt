package amaze.starter.kit

import amaze.starter.kit.AbsoluteDirection.*
import amaze.starter.kit.RelativeDirection.*
import com.github.kittinunf.fuel.gson.jsonBody
import com.github.kittinunf.fuel.gson.responseObject
import com.github.kittinunf.fuel.httpPost

object WallFollower {
    fun solve(facing: AbsoluteDirection) {
        when (val playerStatus = MazeApi.getPlayerStatus()) {
            is PlayerStatus.Exited -> println("You're out!!")
            is PlayerStatus.Running -> doSolve(facing, playerStatus.directionsAvailable)
        }
    }

    private fun doSolve(facing: AbsoluteDirection, directionsAvailable: List<AbsoluteDirection>) {
        val directionToGo = chooseNextStep(directionsAvailable, facing)
        MazeApi.takeTheStep(directionToGo)
        solve(directionToGo)
    }

    private fun chooseNextStep(
        directionsAvailable: List<AbsoluteDirection>,
        facing: AbsoluteDirection
    ): AbsoluteDirection {
        val relativeDirectionsAvailable = directionsAvailable.map { it.toRelative(facing) }

        return when {
            relativeDirectionsAvailable.contains(RIGHT) -> RIGHT.toAbsoluteDirection(facing)
            relativeDirectionsAvailable.contains(FORWARD) -> FORWARD.toAbsoluteDirection(facing)
            relativeDirectionsAvailable.contains(LEFT) -> LEFT.toAbsoluteDirection(facing)
            else -> BACKWARD.toAbsoluteDirection(facing)
        }
    }
}

sealed class PlayerStatus {
    object Exited : PlayerStatus()
    data class Running(val directionsAvailable: List<AbsoluteDirection>) : PlayerStatus()
}

enum class AbsoluteDirection {
    NORTH, EAST, SOUTH, WEST;

    fun toRelative(facing: AbsoluteDirection): RelativeDirection =
        when (facing) {
            NORTH ->
                when (this) {
                    NORTH -> FORWARD
                    EAST -> RIGHT
                    SOUTH -> BACKWARD
                    WEST -> LEFT
                }
            EAST ->
                when (this) {
                    NORTH -> LEFT
                    EAST -> FORWARD
                    SOUTH -> RIGHT
                    WEST -> BACKWARD
                }
            SOUTH ->
                when (this) {
                    NORTH -> BACKWARD
                    EAST -> LEFT
                    SOUTH -> FORWARD
                    WEST -> RIGHT
                }
            WEST ->
                when (this) {
                    NORTH -> RIGHT
                    EAST -> BACKWARD
                    SOUTH -> LEFT
                    WEST -> FORWARD
                }
        }
}

enum class RelativeDirection {
    FORWARD, RIGHT, BACKWARD, LEFT;

    fun toAbsoluteDirection(facing: AbsoluteDirection): AbsoluteDirection =
        when (facing) {
            NORTH ->
                when (this) {
                    FORWARD -> NORTH
                    RIGHT -> EAST
                    BACKWARD -> SOUTH
                    LEFT -> WEST
                }
            EAST ->
                when (this) {
                    FORWARD -> EAST
                    RIGHT -> SOUTH
                    BACKWARD -> WEST
                    LEFT -> NORTH
                }
            SOUTH ->
                when (this) {
                    FORWARD -> SOUTH
                    RIGHT -> WEST
                    BACKWARD -> NORTH
                    LEFT -> EAST
                }
            WEST ->
                when (this) {
                    FORWARD -> WEST
                    RIGHT -> NORTH
                    BACKWARD -> EAST
                    LEFT -> SOUTH
                }
        }
}

object MazeApi {
    private const val PLAYER_NAME = "right-handed-guy"
    private const val PLAYER_TOKEN = "31b4c8d3-60a4-41e5-94f9-24be16cd8e62"
    private const val GAME_ID = "6hjs6b8"

    fun getPlayerStatus(): PlayerStatus {
        val query = """
            query {
                playerStatus(playerName: "$PLAYER_NAME", gameId: "$GAME_ID") {
                    ... on StillIn {
                        directionsAvailable
                    }
                }
            }
        """.trimIndent()
        val directionsAvailable = MazeApi.postQuery(query).directionsAvailable
        return PlayerStatus.Running(directionsAvailable)
    }

    fun takeTheStep(directionToGo: AbsoluteDirection) {
        val mutation = """
            mutation {
                takeAStep(playerName: "$PLAYER_NAME", gameId: "$GAME_ID", stepDirection: ${directionToGo.name}, token: "$PLAYER_TOKEN") {
                  __typename
                }
            }
        """.trimIndent()
        postMutation(mutation)
    }

    private fun postMutation(query: String) {
        "$SERVER_URL/graphql"
            .httpPost()
            .jsonBody(GraphQLQuery(query))
            .responseString()
    }

    private fun postQuery(query: String): DirectionsResponse {
        val (_, _, result) =
            "$SERVER_URL/graphql"
                .httpPost()
                .jsonBody(GraphQLQuery(query))
                .responseObject<Response>()
        return result.fold({ it.data.playerStatus }, { throw RuntimeException("your query failed") })
    }

    data class GraphQLQuery(val query: String)

    data class Response(var data: Data)
    data class Data(var playerStatus: DirectionsResponse)
    data class DirectionsResponse(val directionsAvailable: List<AbsoluteDirection>)
}
