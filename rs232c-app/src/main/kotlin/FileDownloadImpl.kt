import FileTransferApp.Companion.myApp
import core.BinaryDownloadListener
import core.Coder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import utils.DataUtils.Companion.toLong
import java.io.File

class FileDownloadImpl : BinaryDownloadListener {
    companion object {
        private const val LOG = false
    }

    private var downloadFile: File? = null
    private var fileSize: Long = -1
    var downloadsFolder = ""
    private val listeners = mutableListOf<ProgressListener>()

    fun addListener(listener: ProgressListener) {
        listeners.add(listener)
    }

    fun removeListener(listener: ProgressListener) {
        listeners.remove(listener)
    }

    override fun onBinaryDataReceived(data: ByteArray) {
        GlobalScope.launch(Dispatchers.IO) {
            val shouldSendAck: Boolean
            if (downloadFile == null || fileSize < 0) {
                fileSize = data.toLong()
                val fileName = String(data.copyOfRange(Long.SIZE_BYTES, data.size))
                val file = File(fileName)
                if (file.exists()) file.delete()
                file.createNewFile()
                downloadFile = file
                shouldSendAck = true
                listeners.forEach { it.onStartDownload(file) }
            } else {
                val decodedBytes = Coder.decodeByteArray(data)
                if (decodedBytes != null) {
                    downloadFile?.appendBytes(decodedBytes)
                    shouldSendAck = true
                } else {
                    if (LOG) {
                        println("ERROR: decoded is null")
                    }
                    myApp.currentDevice.writeError()
                    return@launch
                }
            }

            val currentFile = downloadFile ?: return@launch
            val currentFileSize = currentFile.length()
            val currentProgress = currentFileSize.toDouble() / fileSize
            listeners.forEach { it.onProgressUpdate(currentProgress) }

            if (currentFileSize == fileSize) {
                listeners.forEach { it.onEndDownload(currentFile) }
                if (LOG) {
                    println("DOWNLOADED ${downloadFile?.name} with $fileSize")
                }
                downloadFile = null
                fileSize = -1
            }
            if (shouldSendAck) {
                myApp.currentDevice.writeAck()
            }
        }
    }
}