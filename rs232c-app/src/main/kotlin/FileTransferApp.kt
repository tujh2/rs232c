import jssc.SerialPort
import tornadofx.*
import views.MainWindowView

class FileTransferApp : App(MainWindowView::class) {

    companion object {
        lateinit var myApp: FileTransferApp
    }

    var currentDeviceName: String = ""
    var currentSpeed: Int = SerialPort.BAUDRATE_110

    override fun init() {
        super.init()
        myApp = this
    }
}

fun main(args: Array<String>) {
    launch<FileTransferApp>(args)
}