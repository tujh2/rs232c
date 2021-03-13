import java.io.File

interface ProgressListener {
    fun onStartDownload(file: File)
    fun onProgressUpdate(progress: Double)
    fun onEndDownload(file: File)
}