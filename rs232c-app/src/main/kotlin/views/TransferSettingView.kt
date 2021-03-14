package views

import FileTransferApp.Companion.myApp
import ProgressListener
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.control.Alert
import javafx.scene.control.ButtonType
import javafx.scene.control.Label
import javafx.scene.control.ProgressBar
import tornadofx.*
import javafx.stage.FileChooser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.javafx.JavaFx
import kotlinx.coroutines.launch
import java.io.File

class TransferSettingView : View(), ProgressListener {
    private lateinit var selectedFileLabel: Label
    private lateinit var downloadsFolderLabel: Label
    private lateinit var progressBar: ProgressBar
    private lateinit var downloadingFileLabel: Label
    private lateinit var downloadedPercentageLabel: Label

    init {
        myApp.subscribeOnProgressListener(this)
    }

    override val root = vbox(alignment = Pos.TOP_CENTER) {

        hbox(alignment = Pos.TOP_CENTER) {
            vboxConstraints {
                margin = Insets(10.0, 5.0, 0.0, 5.0)
            }

            button("Select file to transfer") {
                action {
                    val filter = arrayOf(FileChooser.ExtensionFilter("Empty filter", "*"))
                    val fileList = chooseFile("Select file to transfer", filter, mode = FileChooserMode.Single)
                    if (fileList.isNotEmpty()) {
                        myApp.transferFile = fileList[0]
                        selectedFileLabel.text = "Selected files:${myApp.transferFile.name}"
                    } else {
                        selectedFileLabel.text = "Not selected file"
                    }
                }
            }
            button("SEND") {
                action { myApp.sendSelectedFile() }
            }
        }
        label("Not selected file") { selectedFileLabel = this }

        button("Select Downloads folder") {
            vboxConstraints {
                margin = Insets(10.0, 5.0, 0.0, 5.0)
            }
            action {
                val folder = chooseDirectory()

                if (folder != null) {
                    downloadsFolderLabel.text = "${folder.absolutePath}${File.separator}"
                    myApp.downloadsFolder = "${folder.absolutePath}${File.separator}"
                }
            }
        }
        label(myApp.downloadsFolder) { downloadsFolderLabel = this }

        borderpane {
            vboxConstraints {
                margin = Insets(10.0, 20.0, 0.0, 20.0)
            }

            top = label {
                downloadingFileLabel = this
                isVisible = false
                useMaxWidth = true
            }

            center = progressbar {
                progressBar = this
                useMaxWidth = true
                isVisible = false
            }

            right = label {
                downloadedPercentageLabel = this
                isVisible = false
                useMaxWidth = true
                borderpaneConstraints {
                    margin = Insets(0.0, 0.0, 0.0, 5.0)
                }
            }
        }

    }

    override fun onStartDownload(file: File) {
        GlobalScope.launch(Dispatchers.JavaFx) {
            progressBar.progress = 0.0
            progressBar.isVisible = true
            downloadingFileLabel.text = file.name
            downloadingFileLabel.isVisible = true
            downloadedPercentageLabel.text = "0 %"
            downloadedPercentageLabel.isVisible = true
        }
    }

    override fun onProgressUpdate(progress: Double) {
        GlobalScope.launch(Dispatchers.JavaFx) {
            progressBar.progress = progress
            downloadedPercentageLabel.text = "${(progress * 100).toInt()} %"
        }
    }

    override fun onEndDownload(file: File) {
        GlobalScope.launch(Dispatchers.JavaFx) {
            progressBar.isVisible = false
            downloadingFileLabel.isVisible = false
            downloadedPercentageLabel.isVisible = false
            val alert = Alert(Alert.AlertType.INFORMATION, "File \"${file.name}\" with ${file.length()} bytes", ButtonType.OK)
            alert.headerText = "Downloaded successfully!"
            alert.isResizable = true
            alert.initOwner(currentWindow)
            alert.show()
        }
    }


}