package com.github.christopherjung.ka3005p

import com.fazecast.jSerialComm.SerialPortTimeoutException
import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.util.*

class PowerSupplyKA3005P {
    data class Status(val mode : Mode, val currentProtection : Boolean, val voltageProtection : Boolean, val output: Boolean){
        enum class Mode{
            ConstantCurrent, ConstantVoltage
        }
    }

    private companion object{
        fun Float.format(digits: Int) = "%.${digits}f".format(Locale.US, this)
        fun Int.isSet(bit: Int) = (this and (1 shl bit)) != 0
    }

    var ser : SerialConnection? = null
    var input : BufferedInputStream? = null
    var output : BufferedOutputStream? = null

    fun open(portName: String){
        this.ser?.close()
        val ser = SerialConnection()
        ser.open(portName)
        input = BufferedInputStream(ser.getInputStream())
        output = BufferedOutputStream(ser.getOutputStream())
        this.ser = ser
    }

    private fun write(line: String){
        output!!.run {
            write(line.toByteArray(Charsets.UTF_8))
            flush()
        }
    }

    private fun readFrame(maxLength : Int = Int.MAX_VALUE) : String{
        var maxLength = maxLength

        val sb = StringBuilder()
        try {
            while(maxLength > 0){
                val current = input!!.read()
                if(current < 0) break
                sb.append(current.toChar())
                maxLength--
            }
        }catch (e: SerialPortTimeoutException){}

        return sb.toString().trim()
    }

    private fun request(cmd : String, maxLength: Int = Int.MAX_VALUE) : String{
        write(cmd)
        return readFrame(maxLength)
    }

    private fun request(cmd: String, flag: Boolean) : String {
        return request(cmd + (if(flag) 1 else 0))
    }

    fun setOutput(status : Boolean) : String{
        return request("OUT" , status)
    }

    fun setVoltageProtection(status : Boolean) : String{
        return request("OVP" , status)
    }

    fun setCurrentProtection(status : Boolean) : String{
        return request("OCP", status)
    }

    fun setVoltage(voltage : Float){
        request("VSET1:${voltage.format(2)}")
    }

    fun setCurrent(current : Float){
        request("ISET1:${current.format(3)}")
    }

    fun version() : String{
        return request("*IDN?")
    }

    fun getVoltage() : Float{
        return request("VOUT1?", 5).toFloat()
    }

    fun getCurrent() : Float{
        return request("IOUT1?", 5).toFloat()
    }

    fun statusFlags() : Int{
        return request("STATUS?", 1).first().code
    }

    fun status() : Status {
        val flag = statusFlags()
        return Status(
            if(flag.isSet(0)) Status.Mode.ConstantVoltage else Status.Mode.ConstantCurrent,
            flag.isSet(5),
            flag.isSet(7),
            flag.isSet(6)
        )
    }
}