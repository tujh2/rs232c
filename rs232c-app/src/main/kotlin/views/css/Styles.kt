package views.css

import javafx.scene.paint.Paint
import tornadofx.*

class Styles : Stylesheet() {

    companion object {
        val base by cssclass()
        val checkMenuItem by cssclass()

        private val markColor by cssproperty<Paint>("-fx-mark-color")
        private val focusedMarkColor by cssproperty<Paint>("-fx-focused-mark-color")

        object Colors {
            // Gruvbox dark color scheme
            val bg = c("#282828")
            val bg1 = c("#3c3836")
            val bg2 = c("#504945")
            val bg3 = c("#665c54")
            val bg4 = c("#7c6f64")
            val fg = c("#ebdbb2")

            val red = c("#cc241d")
            val green = c("#98971a")
            val yellow = c("#d79921")
            val blue = c("#458588")
            val purple = c("#b16286")
            val aqua = c("#689d6a")
            val gray = c("#a89984")

            val gray1 = c("#928374")
            val red1 = c("#fb4934")
            val green1 = c("#b8bb26")
            val yellow1 = c("#fabd2f")
            val blue1 = c("#83a598")
            val purple1 = c("#d3869b")
            val aqua1 = c("#8ec07c")
        }
    }

    init {
        base {
            backgroundColor += Colors.bg
        }

        menuBar {
            backgroundColor += Colors.bg1
            textFill = Colors.fg
            accentColor = Colors.fg
            label {
                textFill = Colors.fg
            }
        }

        menu {
            backgroundColor += Colors.bg1
            textFill = Colors.fg
            focusedMarkColor.value = Colors.fg
            markColor.value = Colors.fg

            label {
                textFill = Colors.fg
            }

            and(hover) {
                backgroundColor += Colors.yellow
            }
        }

        contextMenu {
            backgroundColor += Colors.bg1
            textFill = Colors.fg
        }

        menuItem {
            backgroundColor += Colors.bg1
            textFill = Colors.fg

            and(hover) {
                backgroundColor += Colors.yellow
            }

            label {
                textFill = Colors.fg
            }

        }

        checkMenuItem {
            focusedMarkColor.value = Colors.fg
            markColor.value = Colors.fg
        }

        val separatorStyle = mixin {
            borderColor += box(Colors.fg)
            borderWidth += box(
                Dimension(0.0, Dimension.LinearUnits.px),
                Dimension(0.0, Dimension.LinearUnits.px),
                Dimension(1.0, Dimension.LinearUnits.px),
                Dimension(0.0, Dimension.LinearUnits.px)
            )
        }

        separator {
            +separatorStyle
            line {
                +separatorStyle
            }
        }

    }
}