import FileTransferApp.Companion.myApp
import core.BinaryUploadListener
import core.Coder
import java.io.BufferedInputStream
import java.io.File
import java.util.concurrent.atomic.AtomicBoolean

class FileUploadThread(var uploadFile: File = File("")) : Runnable, BinaryUploadListener {
    companion object {
        private const val LOG = false
    }

    private var isRunning = AtomicBoolean()
    private val listeners = mutableListOf<ProgressListener>()
    private lateinit var worker: Thread
    private var uploadedSize: Long = 0

    private var uploadReader: BufferedInputStream? = null
    @Volatile
    private var ackReceived: Boolean = false
    @Volatile
    private var errorReceived: Boolean = false
    private var shouldSendFileHeader: Boolean = true
    private val buffer: ByteArray = ByteArray(1024)
    private lateinit var lastUnconfirmedBuffer: ByteArray

    fun addListener(listener: ProgressListener) {
        listeners.add(listener)
    }

    fun removeListener(listener: ProgressListener) {
        listeners.remove(listener)
    }

    fun start() {
        this.uploadReader = uploadFile.inputStream().buffered()
        isRunning.set(false)

        worker = Thread(this)
        shouldSendFileHeader = true
        uploadedSize = 0
        isRunning.set(true)
        worker.start()
        listeners.forEach { it.onSessionStart(uploadFile) }
    }

    fun stop() {
        isRunning.set(false)
        listeners.forEach { it.onSessionEnd(uploadFile) }
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
                    listeners.forEach { it.onError() }
                    myApp.currentDevice.writeBinaryData(lastUnconfirmedBuffer)
                    continue
                }
                if (ackReceived) {
                    if (LOG) {
                        println("ACK RECEIVED(thread)")
                    }
                    ackReceived = false
                    listeners.forEach { it.onProgressUpdate(uploadedSize.toDouble() / uploadFile.length()) }
                    val bytes = uploadReader?.read(buffer)
                    if (bytes != null && bytes > 0) {
                        uploadedSize += bytes
                        lastUnconfirmedBuffer = Coder.codeByteArray(buffer.copyOfRange(0, bytes))
                        //lastUnconfirmedBuffer = buffer.copyOfRange(0, bytes)
                        myApp.currentDevice.writeBinaryData(lastUnconfirmedBuffer)
                    } else {
                        listeners.forEach { it.onSessionEnd(uploadFile) }
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