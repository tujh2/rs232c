package core

import jssc.SerialPort
import jssc.SerialPortEvent
import jssc.SerialPortEventListener
import jssc.SerialPortException
import utils.DataUtils.Companion.readFrames
import utils.DataUtils.Companion.writeFrame

class Connection(deviceName: String, private var currentSpeed: Int, var isMaster: Boolean) {

    private val device = SerialPort(deviceName)
    private val listeners = mutableListOf<ConnectionListener>()
    private val defaultSpeed = SerialPort.BAUDRATE_110

    fun openConnection(): Boolean {
        return try {
            val isOpened = if (!device.isOpened) device.openPort() else true

            // Поднимаем изначально соединение на скорости по-умолчанию
            val isValid = device.setParams(SerialPort.BAUDRATE_110, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE)

            device.addEventListener(PortListener())
            device.isOpened && isOpened && isValid
        } catch (e: SerialPortException) {
            println("${e.methodName}: ${e.portName}")
            false
        }

    }

    fun connect(): Boolean {
        if (!isMaster) return false

        return try {
            device.writeFrame(Frame(Frame.Type.LINK))
                    && device.writeFrame(Frame(Frame.Type.SYNC, syncSpeed = currentSpeed))
        } catch (e: SerialPortException) {
            false
        }
    }

    fun changeMasterSpeed(speed: Int): Boolean {
        if (!isMaster) return false

        currentSpeed = speed
        return try {
            val syncFrameResult = device.writeFrame(Frame(Frame.Type.SYNC, syncSpeed = currentSpeed))
            val isValid = device.setParams(currentSpeed, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE)
            isValid && syncFrameResult
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
                                if (!isMaster)
                                    device.writeFrame(Frame(Frame.Type.LINK))

                                listeners.forEach { it.onConnectionUp() }
                            }
                            Frame.Type.ACK -> {
                            }
                            Frame.Type.SYNC -> {
                                println(frame.syncSpeed)
                                if (frame.syncSpeed < 0 || isMaster)
                                    return
                                listeners.forEach { it.onCurrentSpeedChanged(frame.syncSpeed) }

                                currentSpeed = frame.syncSpeed
                                val isValid = device.setParams(currentSpeed, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE)
                                if (isValid)
                                    device.writeFrame(Frame(Frame.Type.SYNC, syncSpeed = frame.syncSpeed))
                            }
                            Frame.Type.BINARY_DATA -> {
                            }
                            Frame.Type.ERROR -> {
                            }
                            Frame.Type.DOWN_LINK -> {
                                if (!isMaster)
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