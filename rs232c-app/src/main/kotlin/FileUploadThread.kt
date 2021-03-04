import FileTransferApp.Companion.myApp
import core.BinaryUploadListener
import utils.DataUtils.Companion.toByteArray
import java.io.BufferedInputStream
import java.io.File

class FileUploadThread(uploadFile: File) : Thread(), BinaryUploadListener {
    private var uploadReader: BufferedInputStream? = null
    private var ackReceived: Boolean = false
    private var shouldSendName: Boolean = true
    private var shouldSendSize: Boolean = true
    private val fileSize: Long
    private var fileName: String = ""
    private val buffer: ByteArray = ByteArray(1024)

    init {
        this.uploadReader = uploadFile.inputStream().buffered()
        this.fileName = uploadFile.name
        fileSize = uploadFile.length()
    }

    override fun run() {
        while (true) {
            if (uploadReader != null) {
                if (shouldSendName) {
                    shouldSendName = false
                    myApp.currentDevice.writeBinaryData(fileName.toByteArray())
                    continue
                } else if (shouldSendSize && ackReceived) {
                    ackReceived = false
                    shouldSendSize = false
                    myApp.currentDevice.writeBinaryData(fileSize.toByteArray())
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

    override fun onAckReceived() {
        ackReceived = true
    }
}