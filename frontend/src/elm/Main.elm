port module Main exposing (main)

import Browser
import Element exposing (alignRight, centerX, centerY, fill, fillPortion, height, maximum, mouseOver, none, padding, paddingEach, paddingXY, px, width)
import Element.Background as Background
import Element.Border as Border
import Element.Button as Element
import Element.Colours as Colours
import Element.Font as Font
import Element.LoadingSpinner
import Element.Scale as Scale
import Html exposing (Html)
import Json.Decode as Decode
import MazeApi
import Utils.List as ListUtils



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
    | ViewGameClicked MazeApi.GameId



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

        ViewGameClicked gameId ->
            watchGame gameId


handleGameCreationResponse : Result () MazeApi.GameId -> ( Model, Cmd Msg )
handleGameCreationResponse response =
    case response of
        Ok gameId ->
            watchGame gameId

        Err _ ->
            ( SelectingAGame MazeApi.Failed, Cmd.none )


watchGame : MazeApi.GameId -> ( Model, Cmd Msg )
watchGame gameId =
    ( WatchingAGame gameId MazeApi.Loading
    , MazeApi.gameStatus gameId (GameStatusResponseReceived gameId)
    )


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
    Element.layout [ width fill ] <| view_ model


view_ : Model -> Element.Element Msg
view_ model =
    case model of
        SelectingAGame gamesAvailable ->
            Element.row [ width fill, height fill ]
                [ Element.el
                    [ width <| fillPortion 1
                    , height fill
                    , Border.widthEach { right = 1, top = 0, left = 0, bottom = 0 }
                    , Border.color Colours.black
                    , mouseOver [ Background.color Colours.lightGrey ]
                    ]
                    (Element.button [ centerX, centerY ] "START A NEW GAME" StartAGameClicked)
                , Element.el
                    [ width <| fillPortion 1
                    , height fill
                    , mouseOver [ Background.color Colours.lightGrey ]
                    ]
                    (viewGamesAvailable gamesAvailable)
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
                    Element.row [ height fill, width fill, Background.color Colours.lightGrey ]
                        [ viewGameInfo gameId gameStatus_
                        , viewMaze gameStatus_
                        ]


viewGameInfo : MazeApi.GameId -> MazeApi.GameStatus -> Element.Element msg
viewGameInfo gameId gameStatus_ =
    Element.column
        [ Border.widthEach { right = 1, top = 0, left = 0, bottom = 0 }
        , Border.color Colours.black
        , height fill
        ]
        [ Element.el [ padding Scale.small ] (Element.text <| "GAME ID: " ++ MazeApi.toString gameId)
        , viewPlayerNames gameStatus_
        ]


viewGamesAvailable : MazeApi.Webdata (List MazeApi.GameId) -> Element.Element Msg
viewGamesAvailable gamesAvailable =
    case gamesAvailable of
        MazeApi.Loading ->
            Element.text "Loading existing games"

        MazeApi.Failed ->
            Element.text "Could not load existing games"

        MazeApi.Loaded gameIds ->
            Element.column [ centerX, centerY, width fill ] <|
                Element.el [ width fill, Border.widthEach { top = 0, left = 0, right = 0, bottom = 1 }, Border.color Colours.black, paddingXY 0 Scale.medium ] (Element.el [ centerX ] <| Element.text "Join an existing game")
                    :: viewExistingGames gameIds


viewExistingGames : List MazeApi.GameId -> List (Element.Element Msg)
viewExistingGames gameIds =
    ListUtils.chunk 4 gameIds
        |> List.map (\ids -> Element.row [ padding Scale.large, centerX ] (List.map viewExistingGame ids))


viewExistingGame : MazeApi.GameId -> Element.Element Msg
viewExistingGame id =
    Element.el [ padding Scale.medium ] (Element.button [ width <| px 100 ] (MazeApi.toString id) (ViewGameClicked id))


viewMaze : MazeApi.GameStatus -> Element.Element msg
viewMaze gameStatus =
    gameStatus.maze
        |> List.map viewRow
        |> Element.column [ padding Scale.medium, centerX, width fill ]


viewRow : MazeApi.RowOfCells -> Element.Element msg
viewRow row =
    Element.row [ centerX ] <| List.map viewCell row


viewCell : MazeApi.Cell -> Element.Element msg
viewCell cell_ =
    case cell_ of
        MazeApi.Wall ->
            wall

        MazeApi.Floor players ->
            floor players


wall : Element.Element msg
wall =
    emptyCell [ Background.color Colours.black ]


floor : List MazeApi.Playa -> Element.Element msg
floor players =
    if List.isEmpty players then
        emptyCell [ Background.color Colours.white ]

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
        , Border.width 1
        , Border.color Colours.lightGrey
        , Background.color <| toColour <| MazeApi.colour player
        ]
        Element.none


toColour : MazeApi.PlayerColour -> Element.Color
toColour playerColour =
    case playerColour of
        MazeApi.Blue ->
            Colours.blue

        MazeApi.Red ->
            Colours.red

        MazeApi.Green ->
            Colours.yellow

        MazeApi.Purple ->
            Colours.purple

        MazeApi.Brown ->
            Colours.brown


cell : List (Element.Attribute msg) -> Element.Element msg -> Element.Element msg
cell attributes cellContent =
    Element.el
        ([ Element.width (Element.fill |> Element.minimum 12)
         , Element.height (Element.fill |> Element.minimum 12)
         ]
            ++ attributes
        )
        cellContent


emptyCell : List (Element.Attribute msg) -> Element.Element msg
emptyCell attributes =
    cell attributes Element.none


viewPlayerNames : MazeApi.GameStatus -> Element.Element msg
viewPlayerNames gameStatus =
    MazeApi.allPlayersInAGame gameStatus
        |> List.map viewPlayer
        |> Element.column [ width fill, height fill ]


viewPlayer : MazeApi.Playa -> Element.Element msg
viewPlayer player =
    Element.row [ Font.color <| toColour <| MazeApi.colour player, padding Scale.small, alignRight ] [ Element.text <| MazeApi.name player ]
