package views

import tornadofx.View
import tornadofx.vbox

class MainWindowView : View() {
    val menu = find(MenuBar::class)

    override val root = vbox {
        add(MenuBar())
    }
}