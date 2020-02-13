port module Main exposing (main)

import Browser
import Element
import Element.Background
import Element.Input
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


type Msg
    = MazesInformationReceived MazeApi.GameId Decode.Value
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
        MazesInformationReceived gameId mazesInfo ->
            decodeGameStatus gameId mazesInfo model

        StartAGameClicked ->
            ( model, MazeApi.startAGame StartAGameResponseReceived )

        StartAGameResponseReceived response ->
            handleGameCreationResponse response

        ExistingGamesResponseReceived response ->
            ( SelectingAGame <| MazeApi.fromResult response, Cmd.none )


handleGameCreationResponse : Result () MazeApi.GameId -> ( Model, Cmd msg )
handleGameCreationResponse response =
    case response of
        Ok gameId ->
            ( WatchingAGame gameId MazeApi.Loading, Cmd.none )

        Err _ ->
            ( SelectingAGame MazeApi.Loading, Cmd.none )


decodeGameStatus : MazeApi.GameId -> Decode.Value -> Model -> ( Model, Cmd msg )
decodeGameStatus gameId rawGameStatus model =
    case Decode.decodeValue (MazeApi.gameStatusDecoder gameId) rawGameStatus of
        Ok gameStatus_ ->
            ( WatchingAGame gameId (MazeApi.Loaded gameStatus_), Cmd.none )

        Err _ ->
            ( model, Cmd.none )



-- Subscriptions


subscriptions : Model -> Sub Msg
subscriptions model =
    case model of
        WatchingAGame gameId _ ->
            mazesInformationReceived <| MazesInformationReceived gameId

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
                , viewGamesAvailable gamesAvailable
                ]

        WatchingAGame _ _ ->
            Element.none


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
            darkCell

        MazeApi.Floor ->
            lightCell


darkCell : Element.Element msg
darkCell =
    cell [ Element.Background.color <| Element.rgb255 0 0 0 ]


lightCell : Element.Element msg
lightCell =
    cell [ Element.Background.color <| Element.rgb255 255 255 255 ]


cell : List (Element.Attribute msg) -> Element.Element msg
cell attributes =
    Element.el
        ([ Element.width (Element.fill |> Element.minimum 25)
         , Element.height (Element.fill |> Element.minimum 25)
         ]
            ++ attributes
        )
        Element.none
