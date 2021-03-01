package utils

import core.Frame
import jssc.SerialPort

class DataUtils {
    companion object {
        fun byteArrayToInt(bytes: ByteArray): Int {
            var result = 0
            var shift = 0
            for (byte in bytes) {
                result = result or (byte.toInt() shl shift)
                shift += 8
            }
            return result
        }

        fun Int.checkForValidBaudRate(): Int {
            return if (this > SerialPort.BAUDRATE_256000 || this < SerialPort.BAUDRATE_110) -1 else this
        }

        fun SerialPort.writeFrame(frame: Frame): Boolean {
            return this.writeBytes(frame.rawBytes)
        }

        fun SerialPort.readFrame(): Frame {
            return Frame(this.readBytes())
        }
    }
}