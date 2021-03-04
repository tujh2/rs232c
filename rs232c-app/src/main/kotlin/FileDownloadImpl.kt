
import core.BinaryDownloadListener
import utils.DataUtils.Companion.toLong
import java.io.File

class FileDownloadImpl: BinaryDownloadListener {
    private var downloadFile: File? = null
    private var fileSize: Long = -1

    override fun onBinaryDataReceived(data: ByteArray) {
        if (downloadFile?.length() == fileSize) {
            println("DOWNLOADED ${downloadFile?.name} and $fileSize")
            downloadFile = null
            fileSize = -1
            return
        }
        when {
            downloadFile == null -> {
                downloadFile = File(String(data))
            }
            fileSize < 0 -> {
                fileSize = data.toLong()
            }
            else -> {
                downloadFile?.appendBytes(data)
            }
        }
    }
}