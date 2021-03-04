package core

import java.util.*
import kotlin.random.Random


object Coder {
    fun code(byteArray: BitSet): BitSet{
        val c1 = byteArray[0] xor byteArray[1] xor byteArray[3] xor
                byteArray[4] xor byteArray[6] xor
                byteArray[8] xor byteArray[10]

        val c2 = byteArray[0] xor byteArray[2] xor byteArray[3] xor
                byteArray[5] xor byteArray[6] xor
                byteArray[9] xor byteArray[10]

        val c3 = byteArray[1] xor byteArray[2] xor byteArray[3] xor
                byteArray[7] xor byteArray[8] xor
                byteArray[9] xor byteArray[10]

        val c4 = byteArray[4] xor byteArray[5] xor byteArray[6] xor
                byteArray[7] xor byteArray[8] xor
                byteArray[9] xor byteArray[10]

        val outputByteArray = BitSet(15)

        outputByteArray.or(byteArray)
        outputByteArray.set(11,c1)
        outputByteArray.set(12,c2)
        outputByteArray.set(13,c3)
        outputByteArray.set(14,c4)

        return outputByteArray
    }

    fun error(byteArray: BitSet): BitSet{
        val error = "000000000001111"

        byteArray.set(5,false)
        byteArray.set(12,false)

        /*for (i:Int in 0..Random.nextInt(0,15))
                byteArray.set(Random.nextInt(0,15),false)
        */


        return byteArray
    }

    fun decode(byteArray: BitSet): BitSet? {
        val h1 = byteArray[0] xor byteArray[1] xor byteArray[3] xor
                byteArray[4] xor byteArray[6] xor
                byteArray[8] xor byteArray[10] xor byteArray[11]

        if (h1) return null

        val h2 = byteArray[0] xor byteArray[2] xor byteArray[3] xor
                byteArray[5] xor byteArray[6] xor
                byteArray[9] xor byteArray[10] xor byteArray[12]

        if (h2) return null

        val h3 = byteArray[1] xor byteArray[2] xor byteArray[3] xor
                byteArray[7] xor byteArray[8] xor
                byteArray[9] xor byteArray[10] xor byteArray[13]

        if (h3) return null

        val h4 = byteArray[4] xor byteArray[5] xor byteArray[6] xor
                byteArray[7] xor byteArray[8] xor
                byteArray[9] xor byteArray[10] xor byteArray[14]

        if (h4) return null

        val outputByteArray = BitSet(11)

        outputByteArray.or(byteArray.get(0,12))

        return outputByteArray
    }


}