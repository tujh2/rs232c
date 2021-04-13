package core

interface BinaryUploadListener {
    fun onAckReceived()
    fun onErrorReceived()
    fun onDownLink()
}

interface  BinaryDownloadListener {
    fun onBinaryDataReceived(data: ByteArray)
    fun onFileHeaderReceived(data: ByteArray)
    fun onDownLink()
}