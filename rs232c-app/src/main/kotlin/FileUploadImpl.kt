import FileTransferApp.Companion.myApp
import core.BinaryUploadListener
import core.Coder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import utils.DataUtils.Companion.toByteArray
import java.io.BufferedInputStream
import java.io.File
import java.util.concurrent.atomic.AtomicBoolean

class FileUploadThread(private val uploadFile: File) : Runnable, BinaryUploadListener {
    companion object {
        private const val LOG = false
    }

    private var isRunning = AtomicBoolean()
    private lateinit var worker: Thread

    private var uploadReader: BufferedInputStream? = null
    @Volatile
    private var ackReceived: Boolean = false
    @Volatile
    private var errorReceived: Boolean = false
    private var shouldSendFileHeader: Boolean = true
    private val buffer: ByteArray = ByteArray(1024)
    private lateinit var lastUnconfirmedBuffer: ByteArray

    fun start() {
        this.uploadReader = uploadFile.inputStream().buffered()
        isRunning.set(false)

        worker = Thread(this)
        shouldSendFileHeader = true
        isRunning.set(true)
        worker.start()
    }

    fun stop() {
        isRunning.set(false)
    }

    override fun run() {
        while (isRunning.get()) {
            if (uploadReader != null) {
                if (shouldSendFileHeader) {
                    shouldSendFileHeader = false
                    myApp.currentDevice.writeFileHeader(uploadFile)
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
                        println("ACK RECEIVED(thread)")
                    }
                    ackReceived = false
                    val bytes = uploadReader?.read(buffer)
                    if (bytes != null && bytes > 0) {
                        lastUnconfirmedBuffer = Coder.codeByteArray(buffer.copyOfRange(0, bytes))
                        //lastUnconfirmedBuffer = buffer.copyOfRange(0, bytes)
                        myApp.currentDevice.writeBinaryData(lastUnconfirmedBuffer)
                    } else {
                        uploadReader?.close()
                        uploadReader = null
                        isRunning.set(false)
                    }
                }
            }
        }
    }

    override fun onAckReceived() { ackReceived = true }

    override fun onErrorReceived() { errorReceived = true }
}