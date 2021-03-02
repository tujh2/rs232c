package core

import FileTransferApp.Companion.myApp
import jssc.SerialPort
import jssc.SerialPortEvent
import jssc.SerialPortEventListener
import jssc.SerialPortException
import utils.DataUtils.Companion.readFrames
import utils.DataUtils.Companion.writeFrame

class Connection(deviceName: String, private val connectionSpeed: Int) {

    private val device = SerialPort(deviceName)
    private val listeners = mutableListOf<ConnectionListener>()

    fun openConnection(): Boolean {
        return try {
            val isOpened = if (!device.isOpened) device.openPort() else true

            val isValid = device.setParams(connectionSpeed, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE)

            device.addEventListener(PortListener())
            device.isOpened && isOpened && isValid
        } catch (e: SerialPortException) {
            println("${e.methodName}: ${e.portName}")
            false
        }

    }

    fun connect(): Boolean {
        return try {
            device.writeFrame(Frame(Frame.Type.LINK))
                    && device.writeFrame(Frame(Frame.Type.SYNC, syncSpeed = myApp.currentSpeed))
        } catch (e: SerialPortException) {
            false
        }
    }

    fun disconnect(): Boolean {
        return try {
            device.writeFrame(Frame(Frame.Type.DOWN_LINK))
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

    fun addListener(listener: ConnectionListener) {
        listeners.add(listener)
    }

    fun removeListener(listener: ConnectionListener) {
        listeners.remove(listener)
    }

    private inner class PortListener: SerialPortEventListener {
        override fun serialEvent(event: SerialPortEvent?) {
            if (event == null) {
                return
            }
            println("EVENT(${event.portName}): ${event.eventType} ${event.eventValue}")

            when (event.eventType) {
                SerialPortEvent.RXCHAR -> {
                    device.readFrames().forEach { frame ->
                        println("FRAME(${event.portName}): ${frame.type}")
                        when (frame.type) {
                            Frame.Type.LINK -> {
                                if (!myApp.isMaster)
                                    device.writeFrame(Frame(Frame.Type.LINK))

                                listeners.forEach { it.onConnectionUp() }
                            }
                            Frame.Type.ACK -> {
                            }
                            Frame.Type.SYNC -> {
                                if (frame.syncSpeed < 0 || myApp.isMaster)
                                    return
                                listeners.forEach { it.onCurrentSpeedChanged(frame.syncSpeed) }

                                device.writeFrame(Frame(Frame.Type.SYNC, syncSpeed = frame.syncSpeed))
                            }
                            Frame.Type.BINARY_DATA -> {
                            }
                            Frame.Type.ERROR -> {
                            }
                            Frame.Type.DOWN_LINK -> {
                                if (!myApp.isMaster)
                                    device.writeFrame(Frame(Frame.Type.DOWN_LINK))

                                listeners.forEach { it.onConnectionDown() }
                            }
                            Frame.Type.UNKNOWN -> {
                            }
                        }
                    }
                }
                SerialPortEvent.TXEMPTY -> {

                }
            }
        }

    }
}