port module Main exposing (main)

import Browser
import Element exposing (fill, height, width)
import Element.Background as Background
import Element.Colours as Element
import Element.Font as Font
import Element.Input
import Element.LoadingSpinner
import Html exposing (Html)
import Json.Decode as Decode
import MazeApi



-- Program


main : Program () Model Msg
main =
    Browser.element
        { init = init
        , update = update
        , view = view
        , subscriptions = subscriptions
        }


type Model
    = WatchingAGame MazeApi.GameId (MazeApi.Webdata MazeApi.GameStatus)
    | SelectingAGame (MazeApi.Webdata (List MazeApi.GameId))



-- Idea - perhaps I should segregate WatchingAGame and SelectingAGame messages?
-- so the union would be type Msg = WatchingMsg WatchingAGameMsg | SelectingMsg SelectingAGameMsg
-- that would possibly clean the update function


type Msg
    = RawGameStatusReceived MazeApi.GameId Decode.Value
    | GameStatusResponseReceived MazeApi.GameId (Result () MazeApi.GameStatus)
    | ExistingGamesResponseReceived (Result () (List MazeApi.GameId))
    | StartAGameClicked
    | StartAGameResponseReceived (Result () MazeApi.GameId)



-- Init


init : () -> ( Model, Cmd Msg )
init _ =
    ( initialModel, MazeApi.fetchExistingGames ExistingGamesResponseReceived )


initialModel : Model
initialModel =
    SelectingAGame MazeApi.Loading



-- Update


update : Msg -> Model -> ( Model, Cmd Msg )
update msg model =
    case msg of
        RawGameStatusReceived gameId mazesInfo ->
            decodeGameStatus gameId mazesInfo model

        StartAGameClicked ->
            ( model, MazeApi.startAGame StartAGameResponseReceived )

        StartAGameResponseReceived response ->
            handleGameCreationResponse response

        ExistingGamesResponseReceived response ->
            ( SelectingAGame <| MazeApi.fromResult response, Cmd.none )

        GameStatusResponseReceived gameId response ->
            ( WatchingAGame gameId <| MazeApi.fromResult response, subscribeToGameUpdates gameId )


handleGameCreationResponse : Result () MazeApi.GameId -> ( Model, Cmd Msg )
handleGameCreationResponse response =
    case response of
        Ok gameId ->
            ( WatchingAGame gameId MazeApi.Loading
            , MazeApi.gameStatus gameId (GameStatusResponseReceived gameId)
            )

        Err _ ->
            ( SelectingAGame MazeApi.Failed, Cmd.none )


decodeGameStatus : MazeApi.GameId -> Decode.Value -> Model -> ( Model, Cmd msg )
decodeGameStatus gameId rawGameStatus model =
    case Decode.decodeValue (MazeApi.gameStatusDecoder gameId) rawGameStatus of
        Ok gameStatus_ ->
            ( WatchingAGame gameId (MazeApi.Loaded gameStatus_), Cmd.none )

        Err _ ->
            ( model, Cmd.none )



-- Subscriptions


subscribeToGameUpdates : MazeApi.GameId -> Cmd msg
subscribeToGameUpdates gameId =
    gameUpdates <| MazeApi.gameSubscription gameId


subscriptions : Model -> Sub Msg
subscriptions model =
    case model of
        WatchingAGame gameId _ ->
            mazesInformationReceived <| RawGameStatusReceived gameId

        SelectingAGame _ ->
            Sub.none


port mazesInformationReceived : (Decode.Value -> msg) -> Sub msg


port gameUpdates : String -> Cmd msg



-- View


view : Model -> Html Msg
view model =
    Element.layout [] <| view_ model


view_ : Model -> Element.Element Msg
view_ model =
    case model of
        SelectingAGame gamesAvailable ->
            Element.column []
                [ Element.Input.button []
                    { onPress = Just StartAGameClicked
                    , label = Element.text "start a game"
                    }
                , Element.text "---------------"
                , viewGamesAvailable gamesAvailable
                ]

        WatchingAGame gameId gameStatus ->
            case gameStatus of
                MazeApi.Loading ->
                    Element.row []
                        [ Element.text <| "loading your game (" ++ MazeApi.toString gameId ++ ") "
                        , Element.LoadingSpinner.spinner
                        ]

                MazeApi.Failed ->
                    Element.text "there was an error loading your game - please refresh the page and try again"

                MazeApi.Loaded gameStatus_ ->
                    Element.column []
                        [ Element.text <| "watching game " ++ MazeApi.toString gameId
                        , viewGameStatus gameStatus_
                        , viewPlayers gameStatus_
                        ]


viewGamesAvailable : MazeApi.Webdata (List MazeApi.GameId) -> Element.Element msg
viewGamesAvailable gamesAvailable =
    case gamesAvailable of
        MazeApi.Loading ->
            Element.text "Loading existing games"

        MazeApi.Failed ->
            Element.text "Could not load existing games"

        MazeApi.Loaded gameIds ->
            Element.column [] <|
                Element.text "vvvvvvv Join an existing game vvvvvvv"
                    :: List.map (\id -> Element.text (MazeApi.toString id)) gameIds


viewGameStatus : MazeApi.GameStatus -> Element.Element msg
viewGameStatus gameStatus =
    gameStatus.maze
        |> List.map viewRow
        |> Element.column []


viewRow : MazeApi.RowOfCells -> Element.Element msg
viewRow row =
    Element.row [] <| List.map viewCell row


viewCell : MazeApi.Cell -> Element.Element msg
viewCell cell_ =
    case cell_ of
        MazeApi.Wall ->
            wall

        MazeApi.Floor players ->
            floor players


wall : Element.Element msg
wall =
    emptyCell [ Background.color Element.black ]


floor : List MazeApi.Playa -> Element.Element msg
floor players =
    if List.isEmpty players then
        emptyCell [ Background.color Element.white ]

    else
        players
            |> List.map viewPlayerInMaze
            |> Element.row [ width fill, height fill ]
            |> cell []


viewPlayerInMaze : MazeApi.Playa -> Element.Element msg
viewPlayerInMaze player =
    Element.el
        [ width fill
        , height fill
        , Background.color <| toColour <| MazeApi.colour player
        ]
        Element.none


toColour : MazeApi.PlayerColour -> Element.Color
toColour playerColour =
    case playerColour of
        MazeApi.Blue ->
            Element.blue

        MazeApi.Red ->
            Element.red

        MazeApi.Green ->
            Element.green

        MazeApi.Purple ->
            Element.purple

        MazeApi.Brown ->
            Element.brown


cell : List (Element.Attribute msg) -> Element.Element msg -> Element.Element msg
cell attributes cellContent =
    Element.el
        ([ Element.width (Element.fill |> Element.minimum 25)
         , Element.height (Element.fill |> Element.minimum 25)
         ]
            ++ attributes
        )
        cellContent


emptyCell : List (Element.Attribute msg) -> Element.Element msg
emptyCell attributes =
    cell attributes Element.none


viewPlayers : MazeApi.GameStatus -> Element.Element msg
viewPlayers gameStatus =
    MazeApi.allPlayersInAGame gameStatus
        |> List.map viewPlayer
        |> Element.column []


viewPlayer : MazeApi.Playa -> Element.Element msg
viewPlayer player =
    Element.row [ Font.color <| toColour <| MazeApi.colour player ] [ Element.text <| MazeApi.name player ]
