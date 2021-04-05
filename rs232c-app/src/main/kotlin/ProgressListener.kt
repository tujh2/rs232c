import java.io.File

interface ProgressListener {
    fun onSessionStart(file: File)
    fun onProgressUpdate(progress: Double)
    fun onError()
    fun onSessionEnd(file: File)
}