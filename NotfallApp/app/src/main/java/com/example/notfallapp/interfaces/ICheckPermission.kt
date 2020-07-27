package com.example.notfallapp.interfaces

import android.app.AlertDialog
import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.net.ConnectivityManager
import android.net.wifi.WifiManager
import android.os.Handler
import com.example.notfallapp.R
import java.util.*


interface ICheckPermission {
    fun checkPermissions(context: Context, connectivityManager: ConnectivityManager?, wifi: WifiManager){
        checkInternetAccess(context, connectivityManager, wifi)
        checkBluetoothEnabled(context)
    }

    fun checkInternetAccess(context: Context, connectivityManager: ConnectivityManager?, wifi: WifiManager){
       /* var builder: AlertDialog.Builder = AlertDialog.Builder(context)
        var handler = Handler(context.mainLooper)
        var timer = Timer()
        timer.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                var result = isNetworkAvailable(connectivityManager)
                if (result == false){
                    timer.cancel()
                    handler.post( Runnable {
                        builder = enableInternetAccess(context, connectivityManager, wifi)
                        showAccess(builder)
                    })
                }
            }
        }, 0, 3000)*/
    }


    fun checkBluetoothEnabled(context: Context){
        val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        var builder: AlertDialog.Builder
        val handler = Handler(context.mainLooper)
        val timer = Timer()
        timer.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                val success = isBluetoothEnabled(bluetoothAdapter)
                if(!success){
                    timer.cancel()
                    handler.post{
                        builder = enableBluetooth(context, bluetoothAdapter)
                        showAccess(builder)
                    }

                }
            }
        }, 0, 5000)

    }
    private fun isNetworkAvailable(connectivityManager: ConnectivityManager?): Boolean {
        val activeNetworkInfo = connectivityManager!!.activeNetworkInfo
        return activeNetworkInfo != null && activeNetworkInfo.isConnected
    }

    private fun isBluetoothEnabled(bluetoothAdapter: BluetoothAdapter): Boolean {
        var success = false
        if (bluetoothAdapter.isEnabled) {
            success = true
        }
        return success
    }

    private fun enableInternetAccess(context: Context, connectivityManager: ConnectivityManager?, wifi: WifiManager): AlertDialog.Builder{
        val builder: AlertDialog.Builder = AlertDialog.Builder(context)
        builder.setTitle(context.resources.getString(R.string.confirm))
        builder.setMessage(context.resources.getString(R.string.noInternetAccess))
        builder.setPositiveButton(context.resources.getString(R.string.access)){dialog, which ->
            wifi.setWifiEnabled(true)
            checkInternetAccess(context, connectivityManager, wifi)
        }
        builder.setNegativeButton(context.resources.getString(R.string.cancel)){dialog, which ->
            val result = isNetworkAvailable(connectivityManager)
            if (!result) {
                noInternetAccess(context, connectivityManager, wifi)
            }
            dialog.dismiss()
        }
        return builder
    }

    private fun enableBluetooth(context: Context, bluetoothAdapter: BluetoothAdapter): AlertDialog.Builder{
        val builder: AlertDialog.Builder = AlertDialog.Builder(context)
        builder.setTitle(context.resources.getString(R.string.confirm))
        builder.setMessage(context.resources.getString(R.string.noBluetooth))
        builder.setPositiveButton(context.resources.getString(R.string.access)){dialog, which ->
            bluetoothAdapter.enable()
            checkBluetoothEnabled(context)
        }
        builder.setNegativeButton(context.resources.getString(R.string.cancel)){dialog, which ->
            val success = isBluetoothEnabled(bluetoothAdapter)
            if (!success){
            noBluetoothEnabled(context, bluetoothAdapter)
            }
            dialog.dismiss()
        }
        return builder
    }

    private fun noInternetAccess(context: Context, connectivityManager: ConnectivityManager?, wifi: WifiManager){
        val builder: AlertDialog.Builder = enableInternetAccess(context, connectivityManager, wifi)
        showAccess(builder)
    }

    private fun noBluetoothEnabled(context: Context, bluetoothAdapter: BluetoothAdapter){
        val builder: AlertDialog.Builder = enableBluetooth(context, bluetoothAdapter)
        showAccess(builder)
    }
    
    private fun showAccess(builder: AlertDialog.Builder){
        val alertDialog: AlertDialog = builder.create()
        alertDialog.show()
    }
}