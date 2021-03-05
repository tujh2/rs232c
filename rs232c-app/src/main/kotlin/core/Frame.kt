package core

import utils.DataUtils.Companion.checkForValidBaudRate
import utils.DataUtils.Companion.toByteArray
import utils.DataUtils.Companion.toInt

class Frame {

    var type: Type
    var data: ByteArray
    var syncSpeed: Int
    val rawBytes: ByteArray
        get() {
            if (type == Type.SYNC) {
                return byteArrayOf(type.code) + Int.SIZE_BYTES.toShort().toByteArray() + syncSpeed.toByteArray()
            }
            return byteArrayOf(type.code) + data.size.toShort().toByteArray() + data
        }


    constructor(raw: ByteArray) {
        type = if (raw.isEmpty()) { Type.UNKNOWN } else { Type.values().firstOrNull { it.code == raw[0] } ?: Type.UNKNOWN }
        data = if (raw.size > 3)  raw.copyOfRange(3, raw.size) else byteArrayOf()
        syncSpeed = data.toInt().checkForValidBaudRate()
    }

    constructor(type: Type, data: ByteArray = byteArrayOf(), syncSpeed: Int = -1) {
        this.type = type
        this.data = data
        this.syncSpeed = syncSpeed
    }

    enum class Type(val code: Byte) {
        LINK(0),
        ACK(1),
        SYNC(2),
        BINARY_DATA(3),
        DOWN_LINK(4),
        ERROR(5),
        UNKNOWN(6)
    }

}