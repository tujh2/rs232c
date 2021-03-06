import FileTransferApp.Companion.myApp
import core.BinaryUploadListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import utils.DataUtils.Companion.toByteArray
import java.io.BufferedInputStream
import java.io.File

class FileUploadImpl(uploadFile: File) : BinaryUploadListener {
    private var uploadReader: BufferedInputStream? = null
    private var ackReceived: Boolean = false
    private var shouldSendFileHeader: Boolean = true
    private val fileSize: Long
    private var fileName: String = ""
    private val buffer: ByteArray = ByteArray(1024)

    init {
        this.uploadReader = uploadFile.inputStream().buffered()
        this.fileName = uploadFile.name
        fileSize = uploadFile.length()
    }

    fun start() {
        GlobalScope.launch(Dispatchers.IO) {
            while (true) {
                if (uploadReader != null) {
                    if (shouldSendFileHeader) {
                        shouldSendFileHeader = false
                        myApp.currentDevice.writeBinaryData(fileSize.toByteArray() + fileName.toByteArray())
                        continue
                    }
                    if (ackReceived) {
                        ackReceived = false
                        val bytes = uploadReader?.read(buffer)
                        if (bytes != null && bytes > 0) {
                            myApp.currentDevice.writeBinaryData(buffer.copyOfRange(0, bytes))
                        } else {
                            uploadReader?.close()
                            uploadReader = null
                            break
                        }
                    }
                }
            }
        }
    }

    override fun onAckReceived() {
        ackReceived = true
    }
}