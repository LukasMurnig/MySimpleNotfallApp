package com.example.notfallapp.connectBracelet

import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattDescriptor
import android.util.Log
import com.example.notfallapp.bll.ReadWriteCharacteristic
import java.util.*

/**
 * Class who start the Process to Read and Write with the Bracelet.
 */
class ProcessQueueExecutor: Thread() {

    companion object{
        val REQUEST_TYPE_READ_CHAR = 1
        val REQUEST_TYPE_WRITE_CHAR = 2
        val REQUEST_TYPE_WRITE_DESCRIPTOR = 3
    }
    private val TAG = ProcessQueueExecutor::class.java.simpleName

    val EXECUTE_DELAY: Long = 1100 // delay in execution


    var processQueueTimer: Timer? = Timer()

    private val processList: MutableList<ReadWriteCharacteristic>? =
        ArrayList<ReadWriteCharacteristic>()

    /**
     * Adds the request to ProcessQueueExecutor
     * @param readWriteCharacteristic
     */
    fun addProcess(readWriteCharacteristic: ReadWriteCharacteristic) {
        processList!!.add(readWriteCharacteristic)
    }

    /**
     * Removes the request from ProcessQueueExecutor
     * @param readWriteCharacteristic
     */
    fun removeProcess(readWriteCharacteristic: ReadWriteCharacteristic?) {
        processList!!.remove(readWriteCharacteristic)
    }

    fun executeProecess() {
        if (!processList!!.isEmpty()) {
            val readWriteCharacteristic: ReadWriteCharacteristic = processList[0]
            val type: Int = readWriteCharacteristic.requestType
            val bluetoothGatt: BluetoothGatt = readWriteCharacteristic.bluetoothGatt
            val parseObject: Any = readWriteCharacteristic.value
            if (type == REQUEST_TYPE_READ_CHAR) {
                val characteristic =
                    parseObject as BluetoothGattCharacteristic
                try {
                    bluetoothGatt.readCharacteristic(characteristic)
                } catch (e: Exception) {
                    Log.e(TAG, "bluetooth exception:" + e.message)
                }
            } else if (type == REQUEST_TYPE_WRITE_CHAR) {
                val characteristic =
                    parseObject as BluetoothGattCharacteristic
                try {
                    bluetoothGatt.writeCharacteristic(characteristic)
                } catch (e: Exception) {
                    Log.e(TAG, "bluetooth exception:" + e.message)
                }
            } else if (type == REQUEST_TYPE_WRITE_DESCRIPTOR) {
                val clientConfig = parseObject as BluetoothGattDescriptor
                try {
                    bluetoothGatt.writeDescriptor(clientConfig)
                } catch (e: Exception) {
                    Log.e(TAG, "bluetooth exception:" + e.message)
                }
            }
            removeProcess(readWriteCharacteristic)
        }
    }

    /**
     * Returns the number of elements in ProcessQueueExecutor
     * @return the number of elements in ProcessQueueExecutor
     */
    fun getSize(): Int {
        return processList?.size ?: 0
    }

    override fun interrupt() {
        super.interrupt()
        if (processQueueTimer != null) {
            processQueueTimer!!.cancel()
        }
    }

    override fun run() {
        super.run()
        processQueueTimer!!.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                executeProecess()
            }
        }, 0, EXECUTE_DELAY)
    }
}