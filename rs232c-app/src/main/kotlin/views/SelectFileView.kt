package views

import FileTransferApp.Companion.myApp
import javafx.geometry.Pos
import javafx.scene.Parent
import javafx.scene.control.CheckMenuItem
import javafx.scene.control.Label
import javafx.scene.control.Menu
import jssc.SerialPort
import jssc.SerialPortList
import tornadofx.*
import javafx.stage.FileChooser
import java.io.File


class SelectFileView :View(){
    private lateinit var selectedFileLabel: Label
    override val root = vbox(alignment = Pos.TOP_CENTER){

        button("Select file to transfer"){
            action {
                val filter = arrayOf(FileChooser.ExtensionFilter("Empty filter","*"))
                val fileList=chooseFile("Select file to transfer",filter,mode=FileChooserMode.Single)
                if (!fileList.isEmpty()) {
                    myApp.transferedFile=fileList[0]
                    selectedFileLabel.text = "Selected files:${myApp.transferedFile.name}"
                }
                else selectedFileLabel.text = "Not selected file"
                }
            }
        label("Not selected file") { selectedFileLabel = this }
    }

}