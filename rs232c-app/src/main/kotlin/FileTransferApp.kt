import core.Connection
import core.ConnectionListener
import javafx.scene.control.Alert
import javafx.stage.Stage
import jssc.SerialPort
import tornadofx.*
import views.MainWindowView
import views.css.Styles
import java.io.File

class FileTransferApp : App(MainWindowView::class, Styles::class) {

    companion object {
        lateinit var myApp: FileTransferApp
    }

    private var uploadThread: FileUploadThread? = null
    private var downloadImpl: FileDownloadImpl = FileDownloadImpl()

    var transferFile: File = File("")
        set(value){
            field = value
            uploadThread = FileUploadThread(value)
            currentDevice.setDataListener(uploadThread!!)
            uploadThread?.start() // TODO: перенести в обработчик кнопки "Отправить файл"
        }

    var downloadsFolder: String = "./"

    var currentDeviceName: String = ""
        set(value) {
            field = value
            onCurrentDeviceChanged()
        }
    var currentMasterSpeed: Int = SerialPort.BAUDRATE_110
        set(value) {
            field = value
            println("CURRENT SPEED HAS CHANGED TO $currentMasterSpeed")
            currentDevice.changeMasterSpeed(currentMasterSpeed)
        }

    val currentDevice = Connection(currentDeviceName, currentMasterSpeed, false)
    var isMaster: Boolean = false
        get() = currentDevice.isMaster
        set(value) {
            field = value
            currentDevice.isMaster = value
        }

    override fun init() {
        super.init()
        myApp = this
        currentDevice.setDataListener(downloadImpl)
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
        currentDevice.disconnect()
    }

    fun onCurrentDeviceChanged() {
        if (currentDevice.changeDevice(currentDeviceName)) {
            alert(Alert.AlertType.ERROR, "Error!", "Check your connection!")
        }
    }

    fun subscribeOnDevice(listener: ConnectionListener) {
        currentDevice.addListener(listener)
    }
}

fun main(args: Array<String>) {
    launch<FileTransferApp>(args)
}