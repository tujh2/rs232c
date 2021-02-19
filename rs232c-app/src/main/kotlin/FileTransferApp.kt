import tornadofx.*
import views.MainWindowView

class FileTransferApp : App(MainWindowView::class) {

    companion object {
        lateinit var myApp: FileTransferApp
    }

    var currentDeviceName: String = ""

    override fun init() {
        super.init()
        myApp = this
    }
}

fun main(args: Array<String>) {
    launch<FileTransferApp>(args)
}