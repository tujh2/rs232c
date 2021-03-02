package utils

import core.Frame
import jssc.SerialPort
import java.nio.ByteBuffer

class DataUtils {
    companion object {
        fun ByteArray.toInt(): Int {
            var result = 0
            var shift = 0
            for (byte in this) {
                result = result shl shift or byte.toInt()
                shift += 8
            }
            return result
        }

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

            var frameStartIndex = 0
            var frameDataSize: Int
            var frameEndIndex: Int

            do {
                frameDataSize = (bytes[frameStartIndex + 2].toInt() or (bytes[frameStartIndex + 1].toInt() shl 8)) and 0xFFFF
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