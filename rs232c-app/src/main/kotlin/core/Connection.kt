package core

import jssc.SerialPort
import jssc.SerialPortEvent
import jssc.SerialPortEventListener
import jssc.SerialPortException
import utils.DataUtils.Companion.readFrame
import utils.DataUtils.Companion.writeFrame

class Connection(deviceName: String, private val connectionSpeed: Int) {

    private val device = SerialPort(deviceName)

    fun openConnection(): Boolean {
        return try {
            val isOpened = if (!device.isOpened) device.openPort() else true

            val isValid = device.setParams(connectionSpeed, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_EVEN)

            device.addEventListener(PortListener())
            device.isOpened && isOpened && isValid
        } catch (e: SerialPortException) {
            println("${e.methodName}: ${e.portName}")
            false
        }

    }

    fun ping(): Boolean {
        return try {
            device.writeFrame(Frame(Frame.Type.LINK))
        } catch (e: SerialPortException) {
            false
        }
    }

    fun closeConnection(): Boolean {
        return try {
            device.closePort()
        } catch (e: SerialPortException) {
            false
        }
    }

    private inner class PortListener: SerialPortEventListener {
        override fun serialEvent(event: SerialPortEvent?) {
            if (event == null) {
                return
            }
            println("EVENT(${event.portName}): ${event.eventType} ${event.eventValue}")

            when (event.eventType) {
                SerialPortEvent.RXCHAR -> {
                    val frame = device.readFrame()
                    println("FRAME(${event.portName}): ${frame.type}")
                    when (frame.type) {
                        Frame.Type.LINK -> {
                            device.writeFrame(Frame(Frame.Type.ACK))
                        }
                        Frame.Type.ACK -> {}
                        Frame.Type.SYNC -> {}
                        Frame.Type.BINARY_DATA -> {}
                        Frame.Type.ERROR -> {}
                        Frame.Type.DOWN_LINK -> {}
                        Frame.Type.UNKNOWN -> {}
                    }
                }
                SerialPortEvent.TXEMPTY -> {

                }
            }
        }

    }
}