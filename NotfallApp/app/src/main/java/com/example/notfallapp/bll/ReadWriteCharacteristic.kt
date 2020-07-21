package com.example.notfallapp.bll

import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattDescriptor

class ReadWriteCharacteristic(var requestType: Int, var bluetoothGatt: BluetoothGatt, var value: Any) {

}