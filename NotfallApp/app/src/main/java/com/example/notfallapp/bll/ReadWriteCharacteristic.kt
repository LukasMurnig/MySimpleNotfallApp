package com.example.notfallapp.bll

import android.bluetooth.BluetoothGatt

/**
 * Class ReadWriteCharacteristic to react to the signals of the bracelet
 */
class ReadWriteCharacteristic(var requestType: Int, var bluetoothGatt: BluetoothGatt, var value: Any) {

}