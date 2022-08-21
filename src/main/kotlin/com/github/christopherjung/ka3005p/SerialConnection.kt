package com.github.christopherjung.ka3005p
import com.fazecast.jSerialComm.SerialPort
import java.io.InputStream
import java.io.OutputStream

class SerialConnection(){
    private var port: SerialPort? = null

    fun open(portName: String){
        port?.closePort()
        port = SerialPort.getCommPort(portName).apply {
            baudRate = 115200
            setComPortTimeouts(SerialPort.TIMEOUT_READ_BLOCKING, 400,1000000000)
            openPort()
        }
    }

    fun getInputStream() : InputStream {
        return port!!.inputStream
    }

    fun getOutputStream() : OutputStream {
        return port!!.outputStream
    }

    fun close() {
        port?.closePort()
    }
}
