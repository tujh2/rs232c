import FileTransferApp.Companion.myApp
import core.BinaryDownloadListener
import core.Coder
import utils.DataUtils.Companion.toLong
import java.io.File
import java.util.concurrent.atomic.AtomicBoolean

class FileDownloadThread : Runnable, BinaryDownloadListener {
    companion object {
        private const val LOG = false
    }

    private lateinit var worker: Thread
    private var downloadFile: File? = null
    private var fileName = ""
    private var fileSize: Long = -1
    private val listeners = mutableListOf<ProgressListener>()
    private val isRunning = AtomicBoolean()

    // Вообще у нас будет 1 или 0 ByteArray всегда, ибо имеется четкая последовательность кадров BINARY_DATA -> ACK
    // Но на всякий случай работаем с массивом, чтобы не терять данные
    private var receivedData = mutableListOf<ByteArray>()

    fun addListener(listener: ProgressListener) {
        listeners.add(listener)
    }

    fun removeListener(listener: ProgressListener) {
        listeners.remove(listener)
    }

    override fun run() {
        while (isRunning.get()) {
            if (fileName.isEmpty() || fileSize < 0) continue
            if (downloadFile == null) {
                val file = File(myApp.downloadsFolder + fileName)
                if (file.exists()) file.delete()
                file.createNewFile()
                downloadFile = file
                listeners.forEach { it.onSessionStart(file) }
                myApp.currentDevice.writeAck()
                continue
            }

            val data = receivedData.firstOrNull() ?: continue
            receivedData.removeFirst()

            val decodedBytes = Coder.decodeByteArray(data, myApp.shouldAddErrors)
            if (decodedBytes != null) {
                downloadFile?.appendBytes(decodedBytes)
            } else {
                listeners.forEach { it.onError() }
                if (LOG) {
                    println("ERROR: decoded is null")
                }
                myApp.currentDevice.writeError()
                continue
            }


            val currentFile = downloadFile
            if (currentFile == null) {
                isRunning.set(false)
                return
            }
            val currentFileSize = currentFile.length()
            val currentProgress = currentFileSize.toDouble() / fileSize
            listeners.forEach { it.onProgressUpdate(currentProgress) }

            if (currentFileSize == fileSize) {
                listeners.forEach { it.onSessionEnd(currentFile) }
                if (LOG) {
                    println("DOWNLOADED ${downloadFile?.name} with $fileSize")
                }
                downloadFile = null
                fileSize = -1
                isRunning.set(false)
            }
            myApp.currentDevice.writeAck()
        }
    }

    override fun onBinaryDataReceived(data: ByteArray) {
        receivedData.add(data)
    }

    override fun onFileHeaderReceived(data: ByteArray) {
        isRunning.set(false)
        downloadFile = null
        fileSize = data.toLong()
        fileName = String(data.copyOfRange(Long.SIZE_BYTES, data.size))

        isRunning.set(true)
        worker = Thread(this)
        worker.start()
    }
}