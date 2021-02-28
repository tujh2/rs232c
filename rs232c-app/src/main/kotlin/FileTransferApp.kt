import core.Connection
import javafx.scene.control.Alert
import javafx.stage.Stage
import jssc.SerialPort
import tornadofx.*
import views.MainWindowView
import views.css.Styles

class FileTransferApp : App(MainWindowView::class, Styles::class) {

    companion object {
        lateinit var myApp: FileTransferApp
    }

    var currentDeviceName: String = ""
        set(value) {
            field = value
            onCurrentDeviceChanged()
        }
    var currentSpeed: Int = SerialPort.BAUDRATE_110
        set(value) {
            field = value
            onCurrentSpeedChanged()
        }

    private var currentDevice = Connection(currentDeviceName, currentSpeed)

    override fun init() {
        super.init()
        myApp = this
    }

    override fun start(stage: Stage) {
        with (stage) {
            minHeight = 400.0
            minWidth = 400.0
            super.start(stage)
        }
    }

    fun ping() {
        if (!currentDevice.ping()) {
            alert(Alert.AlertType.ERROR, "Error!", "Check your connection!")
        }
    }

    fun disconnect() {
        currentDevice.closeConnection()
    }

    private fun onCurrentDeviceChanged() {
        currentDevice.closeConnection()
        currentDevice = Connection(currentDeviceName, currentSpeed)
        if (!currentDevice.openConnection() && currentDeviceName.isNotEmpty()) {
            alert(Alert.AlertType.ERROR, "Error!", "Check your connection!")
        }
    }

    private fun onCurrentSpeedChanged() {
        currentDevice.closeConnection()
        currentDevice = Connection(currentDeviceName, currentSpeed)
        if (!currentDevice.openConnection() && currentDeviceName.isNotEmpty()) {
            alert(Alert.AlertType.ERROR, "Error!", "Check your connection!")
        }
    }
}

fun main(args: Array<String>) {
    launch<FileTransferApp>(args)
}