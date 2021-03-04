package views

import FileTransferApp.Companion.myApp
import javafx.geometry.Pos
import javafx.scene.control.Label
import tornadofx.*
import javafx.stage.FileChooser

class SelectFileView :View(){
    private lateinit var selectedFileLabel: Label
    override val root = vbox(alignment = Pos.TOP_CENTER) {

        button("Select file to transfer") {
            action {
                val filter = arrayOf(FileChooser.ExtensionFilter("Empty filter","*"))
                val fileList = chooseFile("Select file to transfer",filter,mode = FileChooserMode.Single)
                if (fileList.isNotEmpty()) {
                    myApp.transferFile = fileList[0]
                    selectedFileLabel.text = "Selected files:${myApp.transferFile.name}"
                } else {
                    selectedFileLabel.text = "Not selected file"
                }
            }
        }
        label("Not selected file") { selectedFileLabel = this }
    }

}