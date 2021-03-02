package views

import FileTransferApp.Companion.myApp
import javafx.geometry.Pos
import javafx.scene.Parent
import javafx.scene.control.CheckMenuItem
import javafx.scene.control.Menu
import jssc.SerialPort
import jssc.SerialPortList
import tornadofx.*
import javafx.stage.FileChooser





class SelectFileView :View(){

    override val root = vbox(alignment = Pos.TOP_CENTER){
        button("Select file to transfer"){
            action {
                val filter = arrayOf(FileChooser.ExtensionFilter(
                    "Portable Network Graphics (PNG)", "*.png")
                )
                val file=chooseFile("Select file to transfer",filter)
                if(!file.isEmpty())
                    println(file[0].name)
                else println("EMPTY")
            }


        }

    }

}