package views

import FileTransferApp.Companion.myApp
import ProgressListener
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.control.Label
import javafx.scene.control.ProgressBar
import tornadofx.*
import javafx.stage.FileChooser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.javafx.JavaFx
import kotlinx.coroutines.launch
import java.io.File

class TransferSettingView :View(), ProgressListener {
    private lateinit var selectedFileLabel: Label
    private lateinit var downloadsFolderLabel: Label
    private lateinit var progressBar:ProgressBar

    init {
        myApp.subscribeOnProgressListener(this)
    }

    override fun updateProgress(progress:Double) {
        if (!myApp.isMaster) progressBar.progress=progress else progressBar.progress = 1.0
        println("UPDATE $progress")
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

        progressbar(0.0){
            progressBar = this
        }


    }


}