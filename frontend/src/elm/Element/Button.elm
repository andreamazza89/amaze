module Element.Button exposing (button, myLink)

import Element exposing (Element, centerX)
import Element.Background as Background
import Element.Border as Border
import Element.Colours as Colours
import Element.Font as Font
import Element.Input
import Element.Scale as Scale


button : List (Element.Attribute msg) -> String -> msg -> Element msg
button attributes text msg =
    Element.Input.button
        (attributes ++ attributes_)
        { onPress = Just msg
        , label = Element.el [ centerX ] <| Element.text text
        }


myLink : List (Element.Attribute msg) -> String -> String -> Element msg
myLink attributes url label =
    Element.newTabLink
        (attributes ++ attributes_)
        { url = url
        , label = Element.el [ centerX ] <| Element.text label
        }


attributes_ : List (Element.Attribute msg)
attributes_ =
    [ Border.width 2
    , Border.rounded 3
    , Element.pointer
    , Border.color Colours.black
    , Background.color Colours.white
    , Element.alpha 0.7
    , Font.color Colours.black
    , Element.paddingXY Scale.small Scale.small
    , Element.mouseOver
        [ Font.color Colours.blue
        ]
    ]
