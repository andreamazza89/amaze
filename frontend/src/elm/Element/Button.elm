module Element.Button exposing (button)

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
        (attributes
            ++ [ Border.width 2
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
        )
        { onPress = Just msg
        , label = Element.el [ centerX ] <| Element.text text
        }
