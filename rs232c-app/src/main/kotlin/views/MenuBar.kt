package views

import FileTransferApp
import javafx.scene.Parent
import tornadofx.*

class MenuBar : View() {
    private val devices = arrayListOf("COM0", "COM1", "COM2")

    override val root: Parent = menubar {
        menu("Settings") {
            menu("Device") {
                devices.forEach {
                    val name =
                    if (it == (app as FileTransferApp).currentDevice) "(SELECTED) $it" else it
                    item(name).action {  }
                }
            }

            menu("Port speed") {
                item("0")
                item("1")
            }

        }

    }
}