import FileTransferApp.Companion.myApp
import core.BinaryUploadListener
import core.Coder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import utils.DataUtils.Companion.toByteArray
import java.io.BufferedInputStream
import java.io.File

class FileUploadImpl(uploadFile: File) : BinaryUploadListener {
    companion object {
        private const val LOG = false
    }

    private var uploadReader: BufferedInputStream? = null
    private var ackReceived: Boolean = false
    private var errorReceived: Boolean = false
    private var shouldSendFileHeader: Boolean = true
    private val fileSize: Long
    private var fileName: String = ""
    private val buffer: ByteArray = ByteArray(1024)
    private lateinit var lastUnconfirmedBuffer: ByteArray

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

                    if (errorReceived) {
                        if (LOG) {
                            println("ERROR RECEIVED")
                        }
                        errorReceived = false
                        myApp.currentDevice.writeBinaryData(lastUnconfirmedBuffer)
                        continue
                    }
                    if (ackReceived) {
                        if (LOG) {
                            println("ACK RECEIVED")
                        }
                        ackReceived = false
                        val bytes = (uploadReader?.read(buffer))
                        if (bytes != null && bytes > 0) {
                            lastUnconfirmedBuffer = Coder.codeByteArray(buffer.copyOfRange(0, bytes))
                            //lastUnconfirmedBuffer = buffer.copyOfRange(0, bytes)
                            myApp.currentDevice.writeBinaryData(lastUnconfirmedBuffer)
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

    override fun onErrorReceived() {
        errorReceived = true
    }
}