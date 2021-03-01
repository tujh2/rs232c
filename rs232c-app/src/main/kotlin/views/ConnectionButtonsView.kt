package views

import FileTransferApp.Companion.myApp
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.Parent
import tornadofx.*

class ConnectionButtonsView: View() {
    override val root: Parent = hbox(alignment = Pos.TOP_CENTER) {
        button("Check") {
            hboxConstraints {
                margin = Insets(5.0, 5.0, 5.0, 5.0)
            }
        }.action { myApp.ping() }
        button("Disconnect") {
            hboxConstraints {
                margin = Insets(5.0, 5.0, 5.0, 5.0)
            }
        }.action { myApp.disconnect() }
    }
}