package core

interface BinaryUploadListener {
    fun onAckReceived()
}

interface  BinaryDownloadListener {
    fun onBinaryDataReceived(data: ByteArray)
}