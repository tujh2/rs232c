package views

import tornadofx.*
import views.css.Styles

class MainWindowView : View() {

    override val root = vbox {
        addClass(Styles.base)
        add(MenuBar())
        add(ConnectionDetailsView())
        add(TransferSettingView())
        add(UploadProgressView())
        add(DownloadProgressView())
    }
}