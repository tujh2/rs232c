package core

interface ConnectionListener {

    fun onCurrentDeviceChanged()

    fun onCurrentSpeedChanged(speed: Int)

    fun onConnectionUp()

    fun onConnectionDown()
}