package com.example.notfallapp.interfaces

import android.app.AlertDialog
import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.content.Intent
import android.location.LocationManager
import android.net.ConnectivityManager
import android.net.wifi.WifiManager
import android.os.Handler
import androidx.core.content.ContextCompat.startActivity
import com.example.notfallapp.R
import java.util.*


interface checkPermission {
    fun checkPermissions(context: Context, connectivityManager: ConnectivityManager?, wifi: WifiManager){
        checkInternetAccess(context, connectivityManager, wifi)
        checkBluetoothEnabled(context)
    }

    fun checkInternetAccess(context: Context, connectivityManager: ConnectivityManager?, wifi: WifiManager){
        var builder: AlertDialog.Builder = AlertDialog.Builder(context)
        var handler = Handler(context.mainLooper)
        var timer = Timer()
        timer.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                var result = isNetworkAvailable(connectivityManager)
                if (result == false){
                    timer.cancel()
                    handler.post( Runnable {
                        builder = enableInternetAccess(context, wifi)
                        showAccess(builder)
                    })
                }
            }
        }, 0, 1000)
    }


    fun checkBluetoothEnabled(context: Context){
        var bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        var builder: AlertDialog.Builder = AlertDialog.Builder(context)
        var handler = Handler(context.mainLooper)
        var timer = Timer()
        timer.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                var success = isBluetoothEnabled(bluetoothAdapter)
                if(success == false){
                    timer.cancel()
                    handler.post{
                        builder = enableBluetooth(context, bluetoothAdapter)
                        showAccess(builder)
                    }

                }
            }
        }, 0, 1000)

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

    private fun enableInternetAccess(context: Context, wifi: WifiManager): AlertDialog.Builder{
        var builder: AlertDialog.Builder = AlertDialog.Builder(context)
        builder.setTitle(context.resources.getString(R.string.confirm))
        builder.setMessage(context.resources.getString(R.string.noInternetAccess))
        builder.setPositiveButton(context.resources.getString(R.string.access)){dialog, which ->
            wifi.setWifiEnabled(true)
        }
        builder.setNegativeButton(context.resources.getString(R.string.cancel)){dialog, which ->
           noInternetAccess(context, wifi)
        }
        return builder
    }

    private fun enableBluetooth(context: Context, bluetoothAdapter: BluetoothAdapter): AlertDialog.Builder{
        var builder: AlertDialog.Builder = AlertDialog.Builder(context)
        builder.setTitle(context.resources.getString(R.string.confirm))
        builder.setMessage(context.resources.getString(R.string.noInternetAccess))
        builder.setPositiveButton(context.resources.getString(R.string.access)){dialog, which ->
            bluetoothAdapter.enable()
        }
        builder.setNegativeButton(context.resources.getString(R.string.cancel)){dialog, which ->
            noBluetoothEnabled(context, bluetoothAdapter)
        }
        return builder
    }

    private fun noInternetAccess(context: Context, wifi: WifiManager){
        var builder: AlertDialog.Builder = AlertDialog.Builder(context)
        builder = enableInternetAccess(context, wifi)
        showAccess(builder)
    }

    private fun noBluetoothEnabled(context: Context, bluetoothAdapter: BluetoothAdapter){
        var builder: AlertDialog.Builder = AlertDialog.Builder(context)
        builder = enableBluetooth(context, bluetoothAdapter)
        showAccess(builder)
    }
    
    private fun showAccess(builder: AlertDialog.Builder){
        var alertDialog: AlertDialog
        alertDialog = builder.create()
        alertDialog.show()
    }
}