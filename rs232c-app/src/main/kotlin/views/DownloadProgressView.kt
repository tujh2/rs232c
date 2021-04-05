package views

import FileTransferApp.Companion.myApp
import ProgressListener
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.control.Alert
import javafx.scene.control.ButtonType
import javafx.scene.control.Label
import javafx.scene.control.ProgressBar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.javafx.JavaFx
import kotlinx.coroutines.launch
import tornadofx.*
import java.io.File

class DownloadProgressView: View(), ProgressListener {
    private lateinit var progressBar: ProgressBar
    private lateinit var downloadingFileLabel: Label
    private lateinit var downloadedPercentageLabel: Label
    private lateinit var errorsLabel: Label
    private var errorsCount = 0

    override val root = vbox(alignment = Pos.TOP_CENTER) {
        label("Downloading...")
        label("Errors: 0" ) {
            errorsLabel = this
        }
        borderpane {
            vboxConstraints {
                margin = Insets(10.0, 20.0, 0.0, 20.0)
            }

            top = label {
                downloadingFileLabel = this
                useMaxWidth = true
            }

            center = progressbar {
                progressBar = this
                useMaxWidth = true
            }

            right = label {
                downloadedPercentageLabel = this
                useMaxWidth = true
                borderpaneConstraints {
                    margin = Insets(0.0, 0.0, 0.0, 5.0)
                }
            }
        }
    }

    init {
        myApp.subscribeOnDownloadProgressListener(this)
        root.isVisible = false
    }

    override fun onSessionStart(file: File) {
        GlobalScope.launch(Dispatchers.JavaFx) {
            progressBar.progress = 0.0
            errorsCount = 0
            errorsLabel.text = "Errors: 0"
            downloadingFileLabel.text = file.name
            downloadedPercentageLabel.text = "0 %"
            root.isVisible = true
        }
    }

    override fun onProgressUpdate(progress: Double) {
        GlobalScope.launch(Dispatchers.JavaFx) {
            progressBar.progress = progress
            downloadedPercentageLabel.text = "${(progress * 100).toInt()} %"
        }
    }

    override fun onError() {
        GlobalScope.launch(Dispatchers.JavaFx) {
            errorsCount++
            errorsLabel.text = "Errors: $errorsCount"
        }
    }

    override fun onSessionEnd(file: File) {
        GlobalScope.launch(Dispatchers.JavaFx) {
            root.isVisible = false
            val alert = Alert(Alert.AlertType.INFORMATION, "File \"${file.name}\" with ${file.length()} bytes", ButtonType.OK)
            alert.headerText = "Downloaded successfully!"
            alert.isResizable = true
            alert.initOwner(currentWindow)
            alert.show()
        }
    }
}