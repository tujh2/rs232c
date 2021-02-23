import javafx.scene.control.Alert
import javafx.stage.Stage
import jssc.SerialPort
import jssc.SerialPortException
import tornadofx.*
import views.MainWindowView
import views.css.Styles

class FileTransferApp : App(MainWindowView::class, Styles::class) {

    companion object {
        lateinit var myApp: FileTransferApp
    }

    private var currentDevice: SerialPort? = null

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

    private fun onCurrentDeviceChanged() {
        closeCurrentDevice()
        currentDevice = SerialPort(currentDeviceName)
        setCurrentParams()
    }

    private fun onCurrentSpeedChanged() {
        closeCurrentDevice()
        setCurrentParams()
    }

    private fun setCurrentParams() {
        if (currentDevice == null || currentDeviceName.isEmpty()) {
            return
        }

        if (!currentDevice!!.isOpened) {
            openCurrentDevice()
        }

        try {
            currentDevice!!.setParams(currentSpeed, 0, 0, 0)
        } catch (e: SerialPortException) {
            alert(Alert.AlertType.ERROR, "Ошибка!", "Ошибка установки параметров устройства - проверьте подключение!")
        }
    }

    private fun openCurrentDevice() {
        if (currentDeviceName.isEmpty())
            return

        try {
            currentDevice?.openPort()
            currentDevice?.writeInt(10)
        } catch (e: SerialPortException) {
            alert(Alert.AlertType.ERROR, "Ошибка!", "Ошибка установки связи с устройством - проверьте подключение!")
        }
    }

    private fun closeCurrentDevice() {
        try {
            currentDevice?.openPort()
        } catch (e: SerialPortException) {}
    }
}

fun main(args: Array<String>) {
    launch<FileTransferApp>(args)
}