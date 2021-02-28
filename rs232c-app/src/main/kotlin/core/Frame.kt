package core

import utils.DataUtils
import utils.DataUtils.Companion.checkForValidBaudRate

class Frame {

    var type: Type
    var data: ByteArray
    var syncSpeed: Int
    val rawBytes: ByteArray
        get() = byteArrayOf(type.code) + data


    constructor(raw: ByteArray) {
        type = if (raw.isEmpty()) { Type.UNKNOWN } else { Type.values().firstOrNull { it.code == raw[0] } ?: Type.UNKNOWN }
        data = if (raw.size > 1 && type == Type.BINARY_DATA)  raw.copyOfRange(1, raw.lastIndex) else byteArrayOf()
        syncSpeed = if (data.isNotEmpty() && type == Type.SYNC) DataUtils.byteArrayToInt(data).checkForValidBaudRate() else -1
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