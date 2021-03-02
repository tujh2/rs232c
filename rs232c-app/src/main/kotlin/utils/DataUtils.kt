package utils

import core.Frame
import jssc.SerialPort
import java.nio.ByteBuffer

class DataUtils {
    companion object {
        fun ByteArray.toInt(): Int  = if (this.isNotEmpty()) ByteBuffer.wrap(this).int else -1

        fun Int.toByteArray(): ByteArray {
            return ByteBuffer.allocate(Integer.BYTES)
                .putInt(this)
                .array()
        }

        fun Short.toByteArray(): ByteArray {
            return ByteBuffer.allocate(Short.SIZE_BYTES)
                .putShort(this)
                .array()
        }

        fun ByteArray.toShort(): Short = ByteBuffer.wrap(this).short

        fun Int.checkForValidBaudRate(): Int {
            return if (this > SerialPort.BAUDRATE_256000 || this < SerialPort.BAUDRATE_110) -1 else this
        }

        fun SerialPort.writeFrame(frame: Frame): Boolean {
            return this.writeBytes(frame.rawBytes)
        }

        fun SerialPort.readFrames(): List<Frame> {
            val frames = mutableListOf<Frame>()
            val bytes = this.readBytes()

            if (bytes.size < 3) return emptyList()

            //bytes.forEach { print("$it ") }

            var frameStartIndex = 0
            var frameDataSize: Short
            var frameEndIndex: Int

            do {
                frameDataSize = bytes.copyOfRange(frameStartIndex + 1, frameStartIndex + 3).toShort()
                frameEndIndex = frameStartIndex + frameDataSize + 3

                if (frameEndIndex > bytes.size)
                    return frames

                frames.add(Frame(bytes.copyOfRange(frameStartIndex, frameEndIndex)))
                frameStartIndex = frameEndIndex
            } while (frameStartIndex + 2 < bytes.size)

            return frames
        }
    }
}