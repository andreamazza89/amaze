module Main exposing (main)

import Browser
import Html exposing (Html, br, button, div, input, text)
import Html.Attributes exposing (placeholder, style, value)
import Html.Events exposing (onClick, onInput)
import Http
import Json.Decode as Decode
import Json.Encode as Encode


main : Program () Model Msg
main =
    Browser.element
        { init = init
        , update = update
        , view = view
        , subscriptions = always Sub.none
        }


type alias Model =
    { serverUrl : String
    , sampleResponse : Maybe SampleResponse
    , responseError : Bool
    }


type Msg
    = ServerUrlTyped String
    | MakeSampleRequestClicked
    | SampleResponseReceived (Result Http.Error SampleResponse)


type alias SampleResponse =
    { aString : String
    }


init _ =
    ( { serverUrl = ""
      , sampleResponse = Nothing
      , responseError = False
      }
    , Cmd.none
    )


update : Msg -> Model -> ( Model, Cmd Msg )
update msg model =
    case msg of
        ServerUrlTyped url ->
            ( { model | serverUrl = url }, Cmd.none )

        MakeSampleRequestClicked ->
            ( { model | responseError = False }, makeSampleRequest model.serverUrl )

        SampleResponseReceived (Ok response) ->
            ( { model | sampleResponse = Just response, responseError = False }, Cmd.none )

        SampleResponseReceived (Err _) ->
            ( { model | responseError = True }, Cmd.none )


makeSampleRequest : String -> Cmd Msg
makeSampleRequest serverUrl =
    Http.request
        { method = "POST"
        , headers = []
        , url = serverUrl ++ "/graphql"
        , body = Http.jsonBody sampleQuery
        , expect = Http.expectJson SampleResponseReceived sampleQueryDecoder
        , timeout = Nothing
        , tracker = Nothing
        }


sampleQuery : Encode.Value
sampleQuery =
    Encode.object
        [ ( "query"
          , Encode.string """
              query {
                sampleQuery {
                  aString
                }
              }
          """
          )
        ]


sampleQueryDecoder : Decode.Decoder SampleResponse
sampleQueryDecoder =
    Decode.map SampleResponse <|
        Decode.at [ "data", "sampleQuery", "aString" ] Decode.string


view : Model -> Html Msg
view model =
    div []
        [ div [] [ serverAddressInput model.serverUrl ]
        , br [] []
        , div [] [ sampleRequestButton ]
        , br [] []
        , div [] [ viewSampleResponse model ]
        ]


serverAddressInput : String -> Html Msg
serverAddressInput serverUrl =
    input
        [ onInput ServerUrlTyped
        , placeholder "server address (e.g. http://localhost:8080)"
        , value serverUrl
        , style "width" "-webkit-fill-available"
        ]
        []


sampleRequestButton : Html Msg
sampleRequestButton =
    button [ onClick MakeSampleRequestClicked ] [ text "send sample request" ]


viewSampleResponse : Model -> Html msg
viewSampleResponse model =
    if model.responseError then
        text "There was an error making the sample request; please check the server address and your internet connection."

    else
        case model.sampleResponse of
            Just response ->
                div []
                    [ text "awesome, looks like you're all set; here is what came back from the server:"
                    , br [] []
                    , br [] []
                    , text response.aString
                    , br [] []
                    , br [] []
                    , text "now see if you can pull more data from the sample response and show it here!"
                    ]

            Nothing ->
                text "... add the server address and make a sample request"
