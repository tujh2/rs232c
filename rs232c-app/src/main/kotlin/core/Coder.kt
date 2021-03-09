package core

import java.io.File
import java.nio.ByteBuffer
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
        var outputArray = BitSet(11)
        outputArray.or(byteArray.get(0,11))

        for (i:Int in 0..Random.nextInt(0,15))
            outputArray.set(Random.nextInt(0,15),false)

        return outputArray
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

        outputByteArray.or(byteArray.get(0,11))

        return outputByteArray
    }



    fun codeByteArray(bytes: ByteArray):ByteArray{
        val  byteArray = BitSet.valueOf(bytes)
        byteArray.set(bytes.size*8)

        var codedArray = BitSet(15)
        var toIndex = 0
        var fromIndex = 0
        lateinit var codedBytes : BitSet
        if (byteArray.size()%11==0) {
            codedBytes = BitSet(((byteArray.length()-1) / 11) * 15 )
            codedBytes.set(((byteArray.length()-1) / 11) * 15)
        }
        else {
            codedBytes = BitSet((((byteArray.length()-1) / 11) + 1) * 15 )
            codedBytes.set((((byteArray.length()-1) / 11) + 1) * 15)
        }

        println("CodedBytes Length "+(byteArray.length()-1) + "  "+(codedBytes.length()-1) + "  "+ codedBytes.get(0,codedBytes.length()).toByteArray().size)

        while (true){
            if (fromIndex < byteArray.length()-1) {
                codedArray=code(byteArray.get(fromIndex, fromIndex+11))
                for(i in 0..14){
                    codedBytes[toIndex+i]=codedArray[i]
                }
            }
            else {
                break
            }
            fromIndex+=11
            toIndex+=15
        }
        return codedBytes.get(0,codedBytes.length()-1).toByteArray()
    }

    fun decodeByteArray(bytes: ByteArray):ByteArray?{
        val  byteArray = BitSet.valueOf(bytes)
        byteArray.set(bytes.size*8)

        var decodedArray : BitSet? = BitSet(11)
        var toIndex = 0
        var fromIndex = 0
        var decodedBytes  = BitSet((byteArray.length()-1)/15*11)
        decodedBytes.set((byteArray.length()-1)/15*11)
        if (decodedArray != null) {
            println("DECODED LENGHT  "+bytes.size+"  "+(byteArray.length()-1)+"  "+decodedBytes.get(0,decodedBytes.length()).toByteArray().size)
        }
        while (true){
            if (fromIndex + 14 < byteArray.length()-1) {
                decodedArray=decode(byteArray.get(fromIndex, fromIndex+15))
                if (decodedArray==null)
                    return null
                for(i in 0..10){
                    decodedBytes[toIndex+i]=decodedArray[i]
                }
            }
            else {
                decodedArray=decode(byteArray.get(fromIndex,byteArray.length()-1))
                if (decodedArray==null)
                    return null
                for(i in 0..10){
                    decodedBytes[toIndex+i]=decodedArray[i]
                }
                println("LASTLAST"+fromIndex+" ")
                break
            }
            toIndex+=11
            fromIndex+=15
        }
        return decodedBytes.get(0,decodedBytes.length()-1).toByteArray()
    }
}