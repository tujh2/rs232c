package views

import FileTransferApp.Companion.myApp
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.control.Label
import tornadofx.*
import javafx.stage.FileChooser
import java.io.File

class TransferSettingView : View() {
    private lateinit var selectedFileLabel: Label
    private lateinit var downloadsFolderLabel: Label

    override val root = vbox(alignment = Pos.TOP_CENTER) {
        checkbox("Add errors") {
            isSelected = myApp.shouldAddErrors
            action {
                myApp.shouldAddErrors = isSelected
            }
            vboxConstraints {
                margin = Insets(10.0, 0.0, 10.0, 0.0)
            }
        }

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
                        selectedFileLabel.text = "Selected file: ${myApp.transferFile.name}"
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

    }

}