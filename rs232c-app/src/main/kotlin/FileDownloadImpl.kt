
import FileTransferApp.Companion.myApp
import core.BinaryDownloadListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import utils.DataUtils.Companion.toLong
import java.io.File

class FileDownloadImpl: BinaryDownloadListener {
    private var downloadFile: File? = null
    private var fileSize: Long = -1
    var downloadsFolder = ""

    override fun onBinaryDataReceived(data: ByteArray) {
        GlobalScope.launch(Dispatchers.IO) {
            if(downloadFile == null || fileSize < 0) {
                fileSize = data.toLong()
                downloadFile = File(String(data.copyOfRange(Long.SIZE_BYTES, data.size)))
            } else {
                downloadFile?.appendBytes(data)
            }
            if (downloadFile?.length() == fileSize) {
                println("DOWNLOADED ${downloadFile?.name} with $fileSize")
                downloadFile = null
                fileSize = -1
            }
            myApp.currentDevice.writeAck()
        }
    }
}