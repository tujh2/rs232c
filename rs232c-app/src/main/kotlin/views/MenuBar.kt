package views

import FileTransferApp.Companion.myApp
import javafx.scene.Parent
import javafx.scene.control.CheckMenuItem
import javafx.scene.control.Menu
import jssc.SerialPort
import jssc.SerialPortList
import tornadofx.*
import views.css.Styles

class MenuBar : View() {
    companion object {
        private val speeds = arrayListOf(
            SerialPort.BAUDRATE_110,
            SerialPort.BAUDRATE_300,
            SerialPort.BAUDRATE_600,
            SerialPort.BAUDRATE_1200,
            SerialPort.BAUDRATE_4800,
            SerialPort.BAUDRATE_9600,
            SerialPort.BAUDRATE_14400,
            SerialPort.BAUDRATE_19200,
            SerialPort.BAUDRATE_38400,
            SerialPort.BAUDRATE_57600,
            SerialPort.BAUDRATE_115200,
            SerialPort.BAUDRATE_128000,
            SerialPort.BAUDRATE_256000
        )
    }

    private lateinit var devicesMenu: Menu
    private lateinit var speedsMenu: Menu

    override val root: Parent = menubar {

        menu("Settings") {

            item("Refresh").action {
                updateDevices(devicesMenu)
            }

            separator()

            menu("Device") {
                devicesMenu = this
                updateDevices(devicesMenu)
            }

            menu("Port speed") {
                speedsMenu = this
                updateSpeeds(speedsMenu)
            }

        }

    }

    private fun updateDevices(menu: Menu) {
        val ports = SerialPortList.getPortNames()
        if (ports.isEmpty()) {
            myApp.currentDeviceName = ""
        }

        menu.items.clear()
        ports.forEach {
            val item = CheckMenuItem(it)
            item.addClass(Styles.checkMenuItem)
            item.isSelected = myApp.currentDeviceName == it
            item.action {
                myApp.currentDeviceName = it
                updateDevices(menu)
            }
            menu += item
        }
    }

    private fun updateSpeeds(menu: Menu) {
        menu.items.clear()
        speeds.forEach {
            val item = CheckMenuItem(it.toString())
            item.addClass(Styles.checkMenuItem)
            item.isSelected = myApp.currentSpeed == it
            item.action {
                myApp.currentSpeed = it
                updateSpeeds(menu)
            }
            menu += item
        }
    }
}