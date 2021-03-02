package views

import FileTransferApp.Companion.myApp
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.Parent
import javafx.scene.layout.HBox
import tornadofx.*

class ConnectionButtonsView: View() {
    private lateinit var masterButtons: HBox

    override val root: Parent = vbox(alignment = Pos.TOP_CENTER) {
        checkbox("Master") {
            isSelected = myApp.isMaster
            action {
                myApp.isMaster = !myApp.isMaster
                masterButtons.isVisible = myApp.isMaster
            }
            vboxConstraints {
                margin = Insets(10.0, 0.0, 10.0, 0.0)
            }
        }
        hbox(alignment = Pos.TOP_CENTER) {
            masterButtons = this
            isVisible = myApp.isMaster
            button("Connect") {
                hboxConstraints {
                    margin = Insets(5.0, 5.0, 5.0, 5.0)
                }
            }.action { myApp.connect() }
            button("Disconnect") {
                hboxConstraints {
                    margin = Insets(5.0, 5.0, 5.0, 5.0)
                }
            }.action { myApp.disconnect() }
        }

    }
}