module MazeApi exposing
    ( Cell(..)
    , GameId
    , GameStatus
    , Player
    , PlayerColour(..)
    , RowOfCells
    , Webdata(..)
    , allPlayersInAGame
    , colour
    , fetchExistingGames
    , fromResult
    , gameStatus
    , gameStatusDecoder
    , gameSubscription
    , name
    , solvedIt
    , startAGame
    , toString
    )

import Api.Mutation
import Api.Object exposing (GameStatus)
import Api.Object.Floor
import Api.Object.GameStatus
import Api.Object.Maze
import Api.Object.PlayerInfo
import Api.Object.Position
import Api.Object.Wall
import Api.Query
import Api.Scalar
import Api.Subscription as Subscription
import Api.Union
import Api.Union.Cell
import Dict exposing (Dict)
import Graphql.Document
import Graphql.Http
import Graphql.Operation exposing (RootSubscription)
import Graphql.SelectionSet as SelectionSet exposing (SelectionSet)
import Json.Decode as Decode



-- This is the nice data I want to deal with in the app (similar to but not exactly what the api offers)


type alias RowOfCells =
    List Cell


type Cell
    = Wall
    | Floor (List Player)


type Player
    = Player PlayerDetails


type alias PlayerDetails =
    { name : String
    , colour : PlayerColour
    , solvedIt : Bool
    }


type PlayerColour
    = Red
    | Blue
    | Green
    | Purple
    | Brown


nColours : Int -> List PlayerColour
nColours n =
    List.repeat n [ Red, Blue, Green, Purple, Brown ]
        |> List.concat
        |> List.take n


buildPlayers : List ( String, Bool ) -> List Player
buildPlayers playerNames =
    let
        colours =
            nColours <| List.length playerNames
    in
    List.map2 Tuple.pair playerNames colours
        |> List.map buildPlayer


buildPlayer : ( ( String, Bool ), PlayerColour ) -> Player
buildPlayer ( ( name_, solvedIt_ ), colour_ ) =
    Player { name = name_, colour = colour_, solvedIt = solvedIt_ }


colour : Player -> PlayerColour
colour (Player details) =
    .colour details


name : Player -> String
name (Player details) =
    .name details


solvedIt : Player -> Bool
solvedIt (Player details) =
    .solvedIt details


type alias GameId =
    -- Would like to make this opaque
    String


toString : GameId -> String
toString gameId_ =
    gameId_


type alias GameStatus =
    -- Also would probably like to make this opaque
    { maze : List RowOfCells
    }


allPlayersInAGame : GameStatus -> List Player
allPlayersInAGame gameStatus_ =
    List.foldl allPlayersInARow [] gameStatus_.maze


allPlayersInARow : RowOfCells -> List Player -> List Player
allPlayersInARow row playersFromOtherRows =
    List.foldl allPlayersInACell [] row
        |> List.append playersFromOtherRows


allPlayersInACell : Cell -> List Player -> List Player
allPlayersInACell cell playersOnThisRow =
    case cell of
        Wall ->
            playersOnThisRow

        Floor playersOnThisCell ->
            playersOnThisRow ++ playersOnThisCell


toGameId : Api.Scalar.Id -> GameId
toGameId (Api.Scalar.Id apiGameID) =
    apiGameID



-- This is web data


type Webdata data
    = Loading
    | Failed
    | Loaded data


fromResult : Result error data -> Webdata data
fromResult result =
    case result of
        Ok data ->
            Loaded data

        Err _ ->
            Failed



-- This is how the nice data gets into the app


type alias ApiGameStatus =
    { maze : ApiMaze
    , runners : List ApiRunner
    }


type alias ApiMaze =
    { cells : List ApiCell }


type alias ApiRunner =
    { name : String
    , position : ApiPosition
    , solvedIt : Bool
    }


type alias ApiPosition =
    { column : Int
    , row : Int
    }


type ApiCell
    = ApiWall ApiPosition
    | ApiFloor ApiPosition



-- Queries


fetchExistingGames : (Result () (List GameId) -> msg) -> Cmd msg
fetchExistingGames toMsg =
    Api.Query.gamesAvailable
        |> Graphql.Http.queryRequest "/graphql"
        |> Graphql.Http.send (Result.map (List.map toGameId) >> Result.mapError (always ()) >> toMsg)


gameStatus : GameId -> (Result () GameStatus -> msg) -> Cmd msg
gameStatus gameId toMsg =
    Api.Query.gameStatus { gameId = Api.Scalar.Id gameId } gameSelection
        |> Graphql.Http.queryRequest "/graphql"
        |> Graphql.Http.send (Result.mapError (always ()) >> Result.map mapApiGameStatus >> toMsg)



-- Mutations


startAGame : (Result () GameId -> msg) -> Cmd msg
startAGame toMsg =
    Graphql.Http.mutationRequest
        "/graphql"
        Api.Mutation.startAGame
        |> Graphql.Http.send (Result.map toGameId >> Result.mapError (always ()) >> toMsg)



-- Subscriptions


gameSubscription : GameId -> String
gameSubscription gameId =
    gameSubscriptionSelection gameId
        |> Graphql.Document.serializeSubscription



-- Decoders


gameStatusDecoder : GameId -> Decode.Decoder GameStatus
gameStatusDecoder id =
    gameSubscriptionSelection id
        |> SelectionSet.map mapApiGameStatus
        |> Graphql.Document.decoder


mapApiGameStatus : ApiGameStatus -> GameStatus
mapApiGameStatus apiGameStatus =
    GameStatus <| mapCellsIntoRows apiGameStatus



-- Selection sets


gameSubscriptionSelection : GameId -> SelectionSet ApiGameStatus RootSubscription
gameSubscriptionSelection gameId =
    Subscription.gameStatus { gameId = Api.Scalar.Id gameId } gameSelection


gameSelection : SelectionSet ApiGameStatus Api.Object.GameStatus
gameSelection =
    SelectionSet.map2 ApiGameStatus
        (Api.Object.GameStatus.maze mazeSelection)
        (Api.Object.GameStatus.players playerSelection)


playerSelection : SelectionSet ApiRunner Api.Object.PlayerInfo
playerSelection =
    SelectionSet.map3 ApiRunner
        Api.Object.PlayerInfo.playerName
        (Api.Object.PlayerInfo.position positionSelection)
        Api.Object.PlayerInfo.solvedIt


positionSelection : SelectionSet ApiPosition Api.Object.Position
positionSelection =
    SelectionSet.map2 ApiPosition
        Api.Object.Position.x
        Api.Object.Position.y


mazeSelection : SelectionSet ApiMaze Api.Object.Maze
mazeSelection =
    SelectionSet.map ApiMaze (Api.Object.Maze.cells cellSelection)


cellSelection : SelectionSet ApiCell Api.Union.Cell
cellSelection =
    Api.Union.Cell.fragments
        { onWall = wallSelection
        , onFloor = floorSelection
        }


wallSelection : SelectionSet ApiCell Api.Object.Wall
wallSelection =
    SelectionSet.map ApiWall (Api.Object.Wall.position positionSelection)


floorSelection : SelectionSet ApiCell Api.Object.Floor
floorSelection =
    SelectionSet.map ApiFloor (Api.Object.Floor.position positionSelection)



-- Helpers to map from the data the api returns to the nice data we want to deal with


mapCellsIntoRows : ApiGameStatus -> List RowOfCells
mapCellsIntoRows { runners, maze } =
    maze.cells
        |> List.foldl accumulateIntoMapOfRows Dict.empty
        |> Dict.map (\_ apiCells -> fromApiCells runners apiCells)
        |> Dict.values


accumulateIntoMapOfRows : ApiCell -> Dict Int (List ApiCell) -> Dict Int (List ApiCell)
accumulateIntoMapOfRows apiCell rows =
    let
        rowN =
            getRow apiCell
    in
    case Dict.get rowN rows of
        Just _ ->
            Dict.update rowN (Maybe.map (addIdemToTheEnd apiCell)) rows

        Nothing ->
            Dict.insert rowN [ apiCell ] rows


getRow : ApiCell -> Int
getRow cell_ =
    case cell_ of
        ApiWall position ->
            position.row

        ApiFloor position ->
            position.row


addIdemToTheEnd : a -> List a -> List a
addIdemToTheEnd thingToAdd thingToAddTo =
    thingToAddTo ++ [ thingToAdd ]


fromApiCells : List ApiRunner -> List ApiCell -> List Cell
fromApiCells runners =
    List.map <| fromApiCell runners


fromApiCell : List ApiRunner -> ApiCell -> Cell
fromApiCell apiPlayers apiCell =
    case apiCell of
        ApiFloor cellPosition ->
            Floor <| playersAtThisCell cellPosition <| buildPlayasAndTheirPosition apiPlayers

        ApiWall _ ->
            Wall


playersAtThisCell : ApiPosition -> List ( Player, ApiPosition ) -> List Player
playersAtThisCell cellPosition allPlayers =
    allPlayers
        |> List.filter (\( _, position ) -> position == cellPosition)
        |> List.map Tuple.first


buildPlayasAndTheirPosition : List ApiRunner -> List ( Player, ApiPosition )
buildPlayasAndTheirPosition apiPlayers =
    let
        players =
            buildPlayers <| List.map (\p -> ( p.name, p.solvedIt )) apiPlayers

        positions =
            List.map .position apiPlayers
    in
    List.map2 Tuple.pair players positions
