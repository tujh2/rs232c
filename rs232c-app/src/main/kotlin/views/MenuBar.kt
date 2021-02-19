package views

import FileTransferApp.Companion.myApp
import javafx.scene.Parent
import javafx.scene.control.CheckMenuItem
import javafx.scene.control.Menu
import jssc.SerialPortList
import tornadofx.*

class MenuBar : View() {
    private lateinit var menu: Menu

    override val root: Parent = menubar {

        menu("Settings") {

            item("Refresh").action {
                updateDevices(menu)
            }

            separator()

            menu("Device") {
                menu = this
                updateDevices(menu)
            }

            menu("Port speed") {
                item("0")
                item("1")
            }

        }

    }

    private fun updateDevices(menu: Menu) {
        val ports = SerialPortList.getPortNames()
        if (ports.isEmpty()) {
            myApp.currentDeviceName = ""
        } else if (myApp.currentDeviceName.isEmpty()) {
            myApp.currentDeviceName = ports[0]
        }

        menu.items.clear()
        ports.forEach {
            val item = CheckMenuItem(it)
            item.isSelected = myApp.currentDeviceName == it
            println(myApp.currentDeviceName)
            item.action {
                if (myApp.currentDeviceName == it) {
                    myApp.currentDeviceName = ""
                } else {
                    myApp.currentDeviceName = it
                }
                updateDevices(menu)
            }
            menu += item
        }
    }
}