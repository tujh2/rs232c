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
            println("CURRENT SPEED HAS CHANGED TO $currentSpeed")
            currentDevice.changeMasterSpeed(currentSpeed)
        }

    private var currentDevice = Connection(currentDeviceName, currentSpeed, false)
    var isMaster: Boolean = false
        get() = currentDevice.isMaster
        set(value) {
            field = value
            currentDevice.isMaster = value
        }

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

    fun connect() {
        if (!currentDevice.connect()) {
            alert(Alert.AlertType.ERROR, "Error!", "Check your connection!")
        }
    }

    fun disconnect() {
        currentDevice.closeConnection()
    }

    fun onCurrentDeviceChanged() {
        currentDevice.closeConnection()
        currentDevice = Connection(currentDeviceName, currentSpeed, isMaster)
        if (!currentDevice.openConnection() && currentDeviceName.isNotEmpty()) {
            alert(Alert.AlertType.ERROR, "Error!", "Check your connection!")
        }
    }
}

fun main(args: Array<String>) {
    launch<FileTransferApp>(args)
}