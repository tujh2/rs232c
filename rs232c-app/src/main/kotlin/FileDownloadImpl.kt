import FileTransferApp.Companion.myApp
import core.BinaryDownloadListener
import core.Coder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import utils.DataUtils.Companion.toLong
import java.io.File

class FileDownloadImpl : BinaryDownloadListener {
    private var downloadFile: File? = null
    private var fileSize: Long = -1
    var downloadsFolder = ""
    private val listeners = mutableListOf<ProgressListener>()

    var progress: Double = 0.0
        private set(value) {
            field = value
            listeners.forEach { it.updateProgress(value) }
        }

    fun addListener(listener: ProgressListener) {
        listeners.add(listener)
    }

    fun removeListener(listener: ProgressListener) {
        listeners.remove(listener)
    }

    override fun onBinaryDataReceived(data: ByteArray) {
        GlobalScope.launch(Dispatchers.IO) {
            var shouldSendAck = false
            if (downloadFile == null || fileSize < 0) {
                fileSize = data.toLong()
                downloadFile = File(String(data.copyOfRange(Long.SIZE_BYTES, data.size)))
                shouldSendAck = true
            } else {
                val decodedBytes = Coder.decodeByteArray(data)
                if (decodedBytes != null) {
                    downloadFile?.appendBytes(decodedBytes)
                    shouldSendAck = true

                } else {
                    println("ERROR: decoded is null")
                    // TODO: RETRANSMISSION
                }
            }

            val currentFileSize = downloadFile?.length() ?: 0L
            progress = (currentFileSize / fileSize).toDouble()
            if (currentFileSize == fileSize) {
                println("DOWNLOADED ${downloadFile?.name} with $fileSize")
                downloadFile = null
                fileSize = -1
            }
            if (shouldSendAck) {
                myApp.currentDevice.writeAck()
            }
        }
    }
}