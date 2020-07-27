package com.example.notfallapp.interfaces

import android.bluetooth.*
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.notfallapp.MainActivity
import com.example.notfallapp.bll.ReadWriteCharacteristic
import com.example.notfallapp.connectBracelet.AddBraceletActivity
import com.example.notfallapp.connectBracelet.Constants
import com.example.notfallapp.connectBracelet.ProcessQueueExecutor
import com.example.notfallapp.server.ServerApi.Companion.TAG
import com.example.notfallapp.service.ServiceCallAlarm
import java.util.*

interface connectBracelet {

    companion object{
        private var batteryState: String = " "
        var connected = false
        private var context: Context? = null
        var gattBluetooth: BluetoothGatt? = null
        var process: ProcessQueueExecutor = ProcessQueueExecutor()
        var mGattCallbacks: BluetoothGattCallback = object : BluetoothGattCallback(){}
        private var gattService: BluetoothGattService? = null
    }

    fun connect(context: Context, device: BluetoothDevice){
        process = ProcessQueueExecutor()
        //To execute the read and write operation in a queue.
        if (!process.isAlive()) {
            process.start()
        }
        connectBracelet.context = context
        gattBluetooth = device.connectGatt(context, false, mGattCallbacks)
        mGattCallbacks = object : BluetoothGattCallback() {
            override fun onConnectionStateChange(
                gatt: BluetoothGatt,
                status: Int,
                newState: Int
            ) {
                val device = gatt.device
                val deviceAddress = device.address
                try {
                    when (newState) {
                        BluetoothProfile.STATE_CONNECTED -> {
                            //start service discovery
                            connected = true
                            gatt.discoverServices()
                        }
                        BluetoothProfile.STATE_DISCONNECTED -> {
                            connected = false
                            try {
                                close()
                            } catch (e: Exception) {
                                Log.e(TAG, e.message)
                            }
                        }
                        else -> {
                        }
                    }
                } catch (e: NullPointerException) {
                    e.printStackTrace()
                }
            }

            override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
                val device = gatt.device
                val deviceAddress = device.address
                if (status == BluetoothGatt.GATT_SUCCESS) {
                    // Do APP verification as soon as service discovered.
                    // Do APP verification as soon as service discovered.
                    try {
                        appVerification(
                            gatt,
                            getGattChar(
                                gatt,
                                Constants.SERVICE_VSN_SIMPLE_SERVICE,
                                Constants.CHAR_APP_VERIFICATION
                            ),
                            Constants.NEW_APP_VERIFICATION_VALUE
                        )
                    } catch (e: java.lang.Exception) {
                        Log.e(TAG, "exception with app verify:" + e.message)
                    }
                    for (service in gatt.services) {
                        if (service == null || service.uuid == null) {
                            continue
                        }
                        if (Constants.SERVICE_BATTERY_LEVEL.equals(service.uuid)) {
                            //Read the device battery percentage
                            readCharacteristic(
                                gatt,
                                service.getCharacteristic(Constants.CHAR_BATTERY_LEVEL)
                            )
                        }
                    }
                } else {
                    // Service discovery failed close and disconnect the GATT object of the device.
                    gatt.disconnect()
                    gatt.close()
                }
            }

            // CallBack when the response available for registered the notification( Battery Status, Fall Detect, Key Press)
            override fun onCharacteristicChanged(
                gatt: BluetoothGatt,
                characteristic: BluetoothGattCharacteristic
            ) {

//			Intent i = new Intent(Intent.ACTION_CAMERA_BUTTON);
                val keyValue =
                    characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, 0).toString()
                if (characteristic.uuid == Constants.CHAR_DETECTION_NOTIFY) {
                    if (keyValue == "1") {
                        var intent = Intent(Companion.context, ServiceCallAlarm::class.java)
                        Companion.context?.startActivity(intent)
                    } else if (keyValue == "3") {
                        println("2-10 second press release")
                    } else if (keyValue == "4") {
                        println("fallevent")
                    } else if (keyValue == "5") {
                        println("high g event")
                    } else {
                        println(keyValue)
                    }
                }
            }

            // Callback when the response available for Read Characteristic Request
            override fun onCharacteristicRead(
                gatt: BluetoothGatt,
                characteristic: BluetoothGattCharacteristic,
                status: Int
            ) {
                if (status == BluetoothGatt.GATT_SUCCESS) {
                    // Display received battery value.
                    if (Constants.CHAR_BATTERY_LEVEL.equals(characteristic.uuid)) {
                        batteryState =
                            characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, 0)
                                .toString()
                    } else {
                        Log.i(
                            TAG,
                            "received characteristic read:" + characteristic.uuid.toString()
                        )
                    }
                }
            }

            // Callback when the response available for Write Characteristic Request
            override fun onCharacteristicWrite(
                gatt: BluetoothGatt,
                characteristic: BluetoothGattCharacteristic,
                status: Int
            ) {
            }

            // Callback when the response available for Read Descriptor Request
            override fun onDescriptorRead(
                gatt: BluetoothGatt,
                descriptor: BluetoothGattDescriptor,
                status: Int
            ) {
                Log.i(TAG, "received descriptor read:" + descriptor.uuid.toString())
            }

            // Callback when the response available for Write Descriptor Request
            override fun onDescriptorWrite(
                gatt: BluetoothGatt,
                descriptor: BluetoothGattDescriptor,
                status: Int
            ) {
            }

            override fun onReadRemoteRssi(gatt: BluetoothGatt, rssi: Int, status: Int) {
                if (status == BluetoothGatt.GATT_SUCCESS) {
                    val rssiValue = Integer.toString(rssi)
                }
            }
        }
        var intent = Intent(context, MainActivity::class.java)
        context.startActivity(intent)
    }


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

    /**
     * To write the value to BLE Device for APP verification
     * @param BluetoothGatt object of the device.
     * @param BluetoothGattCharacteristic of the device.
     */
    fun appVerification(
        mGatt: BluetoothGatt?,
        ch: BluetoothGattCharacteristic?,
        value: ByteArray?
    ) {
        writeCharacteristic(mGatt, ch!!, value)
    }

    /**
     * To get the characteristic of the corresponding BluetoothGatt object and
     * service UUID and Characteristic UUID.
     * @param BluetoothGatt object of the device.
     * @param Service UUID.
     * @param Characteristic UUID.
     * @return BluetoothGattCharacteristic of the given service and Characteristic UUID.
     */
    fun getGattChar(
        mGatt: BluetoothGatt,
        serviceuuid: UUID?,
        charectersticuuid: UUID?
    ): BluetoothGattCharacteristic? {
        gattService = mGatt.getService(serviceuuid)
        return gattService?.getCharacteristic(charectersticuuid)
    }

    fun close() {
        gattBluetooth?.disconnect()
        gattBluetooth?.close()
        gattBluetooth = null
        process.interrupt()
    }
}