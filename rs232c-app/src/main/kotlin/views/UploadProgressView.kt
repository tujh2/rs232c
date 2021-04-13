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

class UploadProgressView: View(), ProgressListener {
    private lateinit var progressBar: ProgressBar
    private lateinit var uploadingFileLabel: Label
    private lateinit var uploadedPercentageLabel: Label
    private lateinit var errorsLabel: Label
    private var errorsCount = 0

    override val root = vbox(alignment = Pos.TOP_CENTER) {
        label("Uploading...")
        label("Errors: 0" ) {
            errorsLabel = this
        }
        borderpane {
            vboxConstraints {
                margin = Insets(10.0, 20.0, 0.0, 20.0)
            }

            top = label {
                uploadingFileLabel = this
                useMaxWidth = true
            }

            center = progressbar {
                progressBar = this
                useMaxWidth = true
            }

            right = label {
                uploadedPercentageLabel = this
                useMaxWidth = true
                borderpaneConstraints {
                    margin = Insets(0.0, 0.0, 0.0, 5.0)
                }
            }
        }
    }

    init {
        myApp.subscribeOnUploadProgressListener(this)
        root.isVisible = false
    }

    override fun onSessionStart(file: File) {
        GlobalScope.launch(Dispatchers.JavaFx) {
            progressBar.progress = 0.0
            errorsCount = 0
            errorsLabel.text = "Errors: 0"
            uploadingFileLabel.text = file.name
            uploadedPercentageLabel.text = "0 %"
            root.isVisible = true
        }
    }

    override fun onProgressUpdate(progress: Double) {
        GlobalScope.launch(Dispatchers.JavaFx) {
            progressBar.progress = progress
            uploadedPercentageLabel.text = "${(progress * 100).toInt()} %"
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
        }
    }

    override fun onSessionError() {
        GlobalScope.launch(Dispatchers.JavaFx) {
            root.isVisible = false
        }
    }
}