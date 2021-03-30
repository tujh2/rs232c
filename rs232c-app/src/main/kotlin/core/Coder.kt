package core

import java.nio.ByteBuffer
import java.nio.ByteOrder
import kotlin.experimental.and
import kotlin.experimental.or
import kotlin.experimental.xor
import kotlin.random.Random


object Coder {

    private fun code(input: Short): Short {
        val c1 = (input and 1) xor ((input and 2).toInt() shr 1).toShort() xor
                ((input and 8).toInt() shr 3).toShort() xor ((input and 16).toInt() shr 4).toShort() xor
                ((input and 64).toInt() shr 6).toShort() xor ((input and 256).toInt() shr 8).toShort() xor
                ((input and 1024).toInt() shr 10).toShort()
        val c2 = (input and 1) xor ((input and 4).toInt() shr 2).toShort() xor
                ((input and 8).toInt() shr 3).toShort() xor ((input and 32).toInt() shr 5).toShort() xor
                ((input and 64).toInt() shr 6).toShort() xor ((input and 512).toInt() shr 9).toShort() xor
                ((input and 1024).toInt() shr 10).toShort()
        val c3 = ((input and 2).toInt() shr 1).toShort() xor ((input and 4).toInt() shr 2).toShort() xor
                ((input and 8).toInt() shr 3).toShort() xor ((input and 128).toInt() shr 7).toShort() xor
                ((input and 256).toInt() shr 8).toShort() xor ((input and 512).toInt() shr 9).toShort() xor
                ((input and 1024).toInt() shr 10).toShort()

        val c4 = ((input and 16).toInt() shr 4).toShort() xor ((input and 32).toInt() shr 5).toShort() xor
                ((input and 64).toInt() shr 6).toShort() xor ((input and 128).toInt() shr 7).toShort() xor
                ((input and 256).toInt() shr 8).toShort() xor ((input and 512).toInt() shr 9).toShort() xor
                ((input and 1024).toInt() shr 10).toShort()


        return ((input.toInt() shl 4) xor ((c4.toInt() shl 3)) xor ((c3.toInt() shl 2)) xor
                ((c2.toInt() shl 1)) xor ((c1.toInt() shl 0))).toShort()
    }


    private fun decode(input: Short): Short? {

        val h1 = ((input and 1 * 16).toInt() shr 4).toShort() xor ((input and 2 * 16).toInt() shr 1 + 4).toShort() xor
                ((input and 8 * 16).toInt() shr 3 + 4).toShort() xor ((input and 16 * 16).toInt() shr 4 + 4).toShort() xor
                ((input and 64 * 16).toInt() shr 6 + 4).toShort() xor ((input and 256 * 16).toInt() shr 8 + 4).toShort() xor
                ((input and 1024 * 16).toInt() shr 10 + 4).toShort() xor ((input and 1).toInt() shr 0).toShort()
        if (h1 != 0.toShort()) return null

        val h2 = ((input and 1 * 16).toInt() shr 4).toShort() xor ((input and 4 * 16).toInt() shr 2 + 4).toShort() xor
                ((input and 8 * 16).toInt() shr 3 + 4).toShort() xor ((input and 32 * 16).toInt() shr 5 + 4).toShort() xor
                ((input and 64 * 16).toInt() shr 6 + 4).toShort() xor ((input and 512 * 16).toInt() shr 9 + 4).toShort() xor
                ((input and 1024 * 16).toInt() shr 10 + 4).toShort() xor ((input and 2).toInt() shr 1).toShort()
        if (h2 != 0.toShort()) return null

        val h3 =
            ((input and 2 * 16).toInt() shr 1 + 4).toShort() xor ((input and 4 * 16).toInt() shr 2 + 4).toShort() xor
                    ((input and 8 * 16).toInt() shr 3 + 4).toShort() xor ((input and 128 * 16).toInt() shr 7 + 4).toShort() xor
                    ((input and 256 * 16).toInt() shr 8 + 4).toShort() xor ((input and 512 * 16).toInt() shr 9 + 4).toShort() xor
                    ((input and 1024 * 16).toInt() shr 10 + 4).toShort() xor ((input and 4).toInt() shr 2).toShort()

        if (h3 != 0.toShort()) return null

        val h4 =
            ((input and 16 * 16).toInt() shr 4 + 4).toShort() xor ((input and 32 * 16).toInt() shr 5 + 4).toShort() xor
                    ((input and 64 * 16).toInt() shr 6 + 4).toShort() xor ((input and 128 * 16).toInt() shr 7 + 4).toShort() xor
                    ((input and 256 * 16).toInt() shr 8 + 4).toShort() xor ((input and 512 * 16).toInt() shr 9 + 4).toShort() xor
                    ((input and 1024 * 16).toInt() shr 10 + 4).toShort() xor ((input.toInt() and 8) shr 3).toShort()
        if (h4 != 0.toShort()) return null


        return ((input xor (input and 1) xor (input and 2) xor (input and 4) xor (input and 8)).toInt() shr 4).toShort()
    }

    private fun createError(input: Short) = input xor ((1 shl Random(12).nextInt(0, 15)).toShort())


    private fun codeShortByIndex(index: Int, bytes: ByteArray): Short {
        var indexToRead = index
        var byteToRead = index / 8
        var a: Short = 0
        var bitsToRead = 11
        if (byteToRead > bytes.lastIndex)
            return -1
        if (indexToRead + 10 > (bytes.size * 8) - 1) {
            while (indexToRead < bytes.size * 8) {
                if (indexToRead / 8 > byteToRead) byteToRead += 1
                a = ((a.toInt() shl 1) or (bytes[byteToRead].toInt() shr (7 - (indexToRead % 8)) and 1)).toShort()
                indexToRead += 1
                bitsToRead -= 1
            }
        } else {
            while (bitsToRead > 0) {
                if (indexToRead / 8 > byteToRead) byteToRead += 1
                a = ((a.toInt() shl 1) or (bytes[byteToRead].toInt() shr (7 - (indexToRead % 8)) and 1)).toShort()
                indexToRead += 1
                bitsToRead -= 1
            }
        }
        a = code(a)
        a = a xor ((1 shl (15 - bitsToRead)).toShort())
        return a
    }


    private fun writeShortByIndex(index: Int, bytes: ByteArray, short: Short, size: Int): ByteArray {
        var byteToWriteAt = index / 8
        val a: Short = short

        for (i in index until index + size) {
            if (i / 8 > byteToWriteAt) {
                byteToWriteAt += 1
            }
            if (size - 1 - (i - index) > 7 - i % 8) {
                bytes[byteToWriteAt] =
                    bytes[byteToWriteAt] or
                            ((a.toInt() and (1 shl (size - 1 - (i - index)))) shr (size - 1 - (i - index) - (7 - i % 8))).toByte()
            } else {
                bytes[byteToWriteAt] =
                    bytes[byteToWriteAt] or
                            ((a.toInt() and (1 shl (size - 1 - (i - index)))) shl ((7 - i % 8) - (size - 1 - (i - index)))).toByte()
            }
        }
        return bytes
    }


    private fun decodeShortFromBytes(bytes: ByteArray, shouldAddErrors: Boolean): Pair<Short, Int> {
        var a: Short = ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN).short
        var index = 0
        for (i in 4..15) {
            if (((bytes[i / 8].toInt() shr i % 8) and 1) == 1) {
                index = i
            }
        }
        a = a xor (1 shl index).toShort()
        index -= 4
        if (shouldAddErrors &&  Random.nextInt(0, 1000) == 100) {
            a = createError(a)
        }
        a = decode(a) ?: return Pair(0, -1)
        return Pair(a, index)
    }


    fun codeByteArray(bytes: ByteArray): ByteArray {
        var toIndex = 0
        var fromIndex = 0
        var shortByte: Short
        val codedBytes: ByteArray = if ((bytes.size * 8) % 11 == 0) {
            ByteArray((bytes.size * 8 / 11) * 2)
        } else {
            ByteArray(((bytes.size * 8 / 11) + 1) * 2)
        }
        while (toIndex < codedBytes.size - 1) {
            shortByte = codeShortByIndex(bytes = bytes, index = fromIndex)
            codedBytes[toIndex] = (shortByte and 0xff).toByte()
            codedBytes[toIndex + 1] = ((shortByte.toInt() shr 8).toShort() and 0xff).toByte()
            fromIndex += 11
            toIndex += 2
        }
        return codedBytes
    }

    fun decodeByteArray(bytes: ByteArray, shouldAddErrors: Boolean): ByteArray? {
        var decodedBytes = ByteArray(bytes.size / 2 * 11 / 8)
        var toIndex = 0
        var fromIndex = 0
        while (fromIndex <= bytes.lastIndex - 1) {
            val bytesToDecode = bytes.slice(fromIndex..fromIndex + 1).toByteArray()

            val (short, size) = decodeShortFromBytes(bytesToDecode,  shouldAddErrors)
            if (size < 0) return null
            decodedBytes = writeShortByIndex(toIndex, decodedBytes, short, size)
            fromIndex += 2
            toIndex += size
        }
        return decodedBytes
    }
}