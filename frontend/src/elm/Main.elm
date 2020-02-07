port module Main exposing (main)

import Api.Scalar exposing (Id)
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



-- Model


type alias Model =
    { gameId : Maybe (MazeApi.Webdata Api.Scalar.Id)
    , gameInfo2 : Maybe MazeApi.GameStatus
    }


type Msg
    = MazesInformationReceived Api.Scalar.Id Decode.Value
    | StartAGameClicked
    | StartAGameResponseReceived (MazeApi.Webdata Api.Scalar.Id)



-- Init


init : () -> ( Model, Cmd Msg )
init _ =
    ( initialModel, Cmd.none )


initialModel : Model
initialModel =
    { gameId = Nothing
    , gameInfo2 = Nothing
    }



-- Update


update : Msg -> Model -> ( Model, Cmd Msg )
update msg model =
    case msg of
        MazesInformationReceived gameId mazesInfo ->
            decodeMazesInfo gameId mazesInfo model

        StartAGameClicked ->
            ( model, MazeApi.startAGame StartAGameResponseReceived )

        StartAGameResponseReceived response ->
            ( { model | gameId = Just response }, subscribeToGameIfOneWasCreated response )


decodeMazesInfo : Api.Scalar.Id -> Decode.Value -> Model -> ( Model, Cmd msg )
decodeMazesInfo gameId gameInfo model =
    case Decode.decodeValue (MazeApi.gameStatusDecoder gameId) gameInfo of
        Ok stuff_ ->
            ( { model | gameInfo2 = Just stuff_ }, Cmd.none )

        Err _ ->
            ( model, Cmd.none )



-- Subscriptions


subscriptions : Model -> Sub Msg
subscriptions model =
    case model.gameId of
        Just id ->
            case id of
                MazeApi.Success id_ ->
                    mazesInformationReceived <| MazesInformationReceived id_

                _ ->
                    Sub.none

        Nothing ->
            Sub.none


subscribeToGameIfOneWasCreated : MazeApi.Webdata Api.Scalar.Id -> Cmd Msg
subscribeToGameIfOneWasCreated response =
    case response of
        MazeApi.Success id ->
            MazeApi.gameSubscription id
                |> gameUpdates

        _ ->
            Cmd.none


port mazesInformationReceived : (Decode.Value -> msg) -> Sub msg


port gameUpdates : String -> Cmd msg



-- View


view : Model -> Html Msg
view model =
    Element.layout [] <| startAGame_ model


startAGame_ : Model -> Element.Element Msg
startAGame_ model =
    case model.gameId of
        Just gameId ->
            case gameId of
                MazeApi.Loading ->
                    Element.text "creating a game ..."

                MazeApi.Failed ->
                    Element.text "failed to create a game - please refresh the page and try again"

                MazeApi.Success (Api.Scalar.Id id) ->
                    Element.column []
                        [ Element.text <| "GAME ID: " ++ id
                        , Maybe.map viewMaze2 model.gameInfo2 |> Maybe.withDefault Element.none
                        ]

        Nothing ->
            Element.Input.button []
                { onPress = Just StartAGameClicked
                , label = Element.text "start a game"
                }


viewMaze2 : MazeApi.GameStatus -> Element.Element msg
viewMaze2 gameStatus =
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
