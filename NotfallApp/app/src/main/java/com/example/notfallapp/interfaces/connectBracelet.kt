package com.example.notfallapp.interfaces

import android.bluetooth.*
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.notfallapp.bll.ReadWriteCharacteristic
import com.example.notfallapp.connectBracelet.AddBraceletActivity
import com.example.notfallapp.connectBracelet.Constants
import com.example.notfallapp.connectBracelet.ProcessQueueExecutor
import com.example.notfallapp.service.ServiceCallAlarm

interface connectBracelet {

    companion object{
        private var batteryState: String = " "
    }

    var bluetoothGatt: BluetoothGatt?
    var process: ProcessQueueExecutor

    fun connect(context: Context, device: BluetoothDevice){
        process = ProcessQueueExecutor()
        bluetoothGatt = device.connectGatt(context, false, gattCallback)
    }

    // Various callback methods defined by the BLE API.
    val gattCallback: BluetoothGattCallback

    //Read the value of the BLE device
    fun readCharacteristic(
        mGatt: BluetoothGatt?,
        characteristic: BluetoothGattCharacteristic
    ) {
        if (AddBraceletActivity.connected == false) {
            return
        }
        val readWriteCharacteristic = ReadWriteCharacteristic(
            ProcessQueueExecutor.REQUEST_TYPE_READ_CHAR,
            mGatt!!,
            characteristic!!
        )
        process.addProcess(readWriteCharacteristic)
    }
    //To write the value to the BLE device.
    fun writeCharacteristic(
        mGatt: BluetoothGatt?,
        characteristic: BluetoothGattCharacteristic,
        b: ByteArray?
    ) {
        if (AddBraceletActivity.connected == false) {
            return
        }
        characteristic.value = b
        val readWriteCharacteristic = mGatt?.let {
            ReadWriteCharacteristic(
                ProcessQueueExecutor.REQUEST_TYPE_WRITE_CHAR,
                it,
                characteristic
            )
        }
        if (readWriteCharacteristic != null) {
            process.addProcess(readWriteCharacteristic)
        }
    }

    fun close() {
        bluetoothGatt?.disconnect()
        bluetoothGatt?.close()
        bluetoothGatt = null
        process.interrupt()
    }
}