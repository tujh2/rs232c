package core

interface BinaryUploadListener {
    fun onAckReceived()
    fun onErrorReceived()
}

interface  BinaryDownloadListener {
    fun onBinaryDataReceived(data: ByteArray)
}