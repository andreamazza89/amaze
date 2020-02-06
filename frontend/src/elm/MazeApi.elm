module MazeApi exposing
    ( Cell(..)
    , GameId
    , GameStatus
    , RowOfCells
    , Webdata(..)
    , gameStatusDecoder2
    , gameSubscription
    , startAGame
    )

import Api.Mutation
import Api.Object exposing (GameStatus)
import Api.Object.Floor
import Api.Object.GameStatus
import Api.Object.Maze
import Api.Object.PlayerPosition
import Api.Object.Position
import Api.Object.Wall
import Api.Scalar
import Api.Subscription as Subscription
import Api.Union
import Api.Union.Cell
import Dict exposing (Dict)
import Graphql.Document
import Graphql.Http
import Graphql.Operation exposing (RootSubscription)
import Graphql.SelectionSet as SelectionSet exposing (SelectionSet, with)
import Json.Decode as Decode



-- This is the nice data I want to deal with in the app


type alias RowOfCells =
    List Cell


type Cell
    = Wall
    | Floor -- ideally this will carry a list of players


type alias GameId =
    String



-- This is how the nice data gets into the app


type Webdata data
    = Loading
    | Failed
    | Success data


fromResult : Result error data -> Webdata data
fromResult result =
    case result of
        Ok data ->
            Success data

        Err _ ->
            Failed


type alias GameStatusInternal =
    { maze : MazeInternal
    , runners : List RunnerInternal
    }


type alias GameStatus =
    { maze : List RowOfCells
    }


type alias RunnerInternal =
    { name : String
    , position : PositionInternal
    }


type alias PositionInternal =
    { column : Int
    , row : Int
    }


type alias MazeInternal =
    { cells : List CellInternal }


type CellInternal
    = WallInternal PositionInternal
    | FloorInternal PositionInternal


startAGame : (Webdata Api.Scalar.Id -> msg) -> Cmd msg
startAGame toMsg =
    Graphql.Http.mutationRequest
        "http://localhost:8080/graphql"
        Api.Mutation.startAGame
        |> Graphql.Http.send (fromResult >> toMsg)


gameStatusDecoder2 : Api.Scalar.Id -> Decode.Decoder GameStatus
gameStatusDecoder2 id =
    Graphql.Document.decoder (gameSelection2 id)
        |> Decode.andThen theOne


theOne : GameStatusInternal -> Decode.Decoder GameStatus
theOne apiThing =
    Decode.succeed (GameStatus <| makeRows2 apiThing.maze.cells)


gameSelection2 : Api.Scalar.Id -> SelectionSet GameStatusInternal RootSubscription
gameSelection2 gameId =
    Subscription.gameStatus { gameId = gameId } gameInfoSelection


gameSubscription : Api.Scalar.Id -> String
gameSubscription gameId =
    gameSelection gameId
        |> Graphql.Document.serializeSubscription


gameSelection : Api.Scalar.Id -> SelectionSet GameStatusInternal RootSubscription
gameSelection gameId =
    Subscription.gameStatus { gameId = gameId } gameInfoSelection


gameInfoSelection : SelectionSet GameStatusInternal Api.Object.GameStatus
gameInfoSelection =
    SelectionSet.succeed GameStatusInternal
        |> with (Api.Object.GameStatus.maze mazeSelection)
        |> with (Api.Object.GameStatus.playersPositions playerSelection)


playerSelection : SelectionSet RunnerInternal Api.Object.PlayerPosition
playerSelection =
    SelectionSet.succeed RunnerInternal
        |> with Api.Object.PlayerPosition.playerName
        |> with (Api.Object.PlayerPosition.position positionSelection)


positionSelection : SelectionSet PositionInternal Api.Object.Position
positionSelection =
    SelectionSet.succeed PositionInternal
        |> with Api.Object.Position.x
        |> with Api.Object.Position.y


mazeSelection : SelectionSet MazeInternal Api.Object.Maze
mazeSelection =
    SelectionSet.succeed MazeInternal
        |> with (Api.Object.Maze.cells cellSelection)


cellSelection : SelectionSet CellInternal Api.Union.Cell
cellSelection =
    Api.Union.Cell.fragments
        { onWall = wallSelection
        , onFloor = floorSelection
        }


wallSelection : SelectionSet CellInternal Api.Object.Wall
wallSelection =
    SelectionSet.succeed WallInternal
        |> with (Api.Object.Wall.position positionSelection)


floorSelection : SelectionSet CellInternal Api.Object.Floor
floorSelection =
    SelectionSet.succeed FloorInternal
        |> with (Api.Object.Floor.position positionSelection)


makeRows2 : List CellInternal -> List RowOfCells
makeRows2 cells =
    cells
        |> List.foldl accumulateIntoMapOfRows Dict.empty
        |> Dict.map (\_ apiCells -> fromApiCells apiCells)
        |> Dict.values


accumulateIntoMapOfRows : CellInternal -> Dict Int (List CellInternal) -> Dict Int (List CellInternal)
accumulateIntoMapOfRows apiCell rows =
    let
        rowN =
            getRow apiCell
    in
    case Dict.get rowN rows of
        Just _ ->
            Dict.update rowN (\maybeRow -> Maybe.map (\r -> r ++ [ apiCell ]) maybeRow) rows

        Nothing ->
            Dict.insert rowN [ apiCell ] rows


fromApiCells : List CellInternal -> List Cell
fromApiCells cellInternal =
    cellInternal
        |> List.map
            (\c ->
                case c of
                    FloorInternal _ ->
                        Floor

                    WallInternal _ ->
                        Wall
            )


getRow : CellInternal -> Int
getRow cell_ =
    case cell_ of
        WallInternal position ->
            position.row

        FloorInternal position ->
            position.row
