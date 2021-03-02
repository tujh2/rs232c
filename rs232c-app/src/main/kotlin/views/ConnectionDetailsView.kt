package views

import FileTransferApp.Companion.myApp
import core.ConnectionListener
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.Parent
import javafx.scene.control.Label
import javafx.scene.layout.HBox
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.javafx.JavaFx
import kotlinx.coroutines.launch
import tornadofx.*

class ConnectionDetailsView: View(), ConnectionListener {
    private lateinit var masterButtons: HBox
    private lateinit var connectionStatusLabel: Label

    init {
        myApp.subscribeOnDevice(this)
    }

    override val root: Parent = vbox(alignment = Pos.TOP_CENTER) {
        checkbox("Master") {
            isSelected = myApp.isMaster
            action {
                myApp.isMaster = isSelected
                masterButtons.isVisible = myApp.isMaster
            }
            vboxConstraints {
                margin = Insets(10.0, 0.0, 10.0, 0.0)
            }
        }

        label("Disconnected") { connectionStatusLabel = this }

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

    override fun onCurrentDeviceChanged() {}

    override fun onCurrentSpeedChanged(speed: Int) {
        GlobalScope.launch(Dispatchers.JavaFx) {
            connectionStatusLabel.text = "Connected via ${myApp.currentDeviceName} with $speed"
        }
    }

    override fun onConnectionUp() {
        GlobalScope.launch(Dispatchers.JavaFx) {
            connectionStatusLabel.text = "Connected via ${myApp.currentDeviceName} with ${myApp.currentMasterSpeed}"
        }
    }

    override fun onConnectionDown() {
        GlobalScope.launch(Dispatchers.JavaFx) {
            connectionStatusLabel.text = "Disconnected"
        }
    }
}