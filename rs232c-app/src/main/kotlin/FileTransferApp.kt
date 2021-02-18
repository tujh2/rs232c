import tornadofx.*
import views.MainWindowView

class FileTransferApp : App(MainWindowView::class) {
    var currentDevice = "COM0"
}

fun main(args: Array<String>) {
    launch<FileTransferApp>(args)
}