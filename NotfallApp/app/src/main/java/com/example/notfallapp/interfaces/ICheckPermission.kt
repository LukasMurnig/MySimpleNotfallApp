package com.example.notfallapp.interfaces

import android.app.Activity
import android.app.AlertDialog
import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.content.IntentSender.SendIntentException
import android.location.LocationManager
import android.net.ConnectivityManager
import android.net.wifi.WifiManager
import android.os.Handler
import android.util.Log
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import com.example.notfallapp.R
import com.example.notfallapp.server.ServerApi.Companion.TAG
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.PendingResult
import com.google.android.gms.common.api.ResultCallback
import com.google.android.gms.common.api.Status
import com.google.android.gms.location.*
import java.util.*


interface ICheckPermission : INotifications {
    fun checkPermissions(context: Context){
        checkInternetAccess(context)
        checkBluetoothEnabled(context)
        checkGPSEnabled(context)
    }

    fun checkInternetGPSPermissions(context: Context){
        checkInternetAccess(context)
        checkGPSEnabled(context)
    }

    fun checkInternetAccess(context: Context){
        /*val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val wifi =
            context.getSystemService(Context.WIFI_SERVICE) as WifiManager
        var builder: AlertDialog.Builder = AlertDialog.Builder(context)
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

    private fun checkGPSEnabled(context: Context){
        val handler = Handler(context.mainLooper)
        val timer = Timer()
        timer.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                val success = isGPSEnabled(context)
                if(!success){
                    timer.cancel()
                    handler.post{
                        enableGPS(context)
                    }

                }
            }
        }, 0, 3000)

    }
    fun isNetworkAvailable(connectivityManager: ConnectivityManager?): Boolean {
        val activeNetworkInfo = connectivityManager!!.activeNetworkInfo
        return activeNetworkInfo != null && activeNetworkInfo.isConnected
    }

    fun isBluetoothEnabled(bluetoothAdapter: BluetoothAdapter): Boolean {
        var success = false
        if (bluetoothAdapter.isEnabled) {
            success = true
        }
        return success
    }

    fun isGPSEnabled(context: Context):Boolean {
        try{
            var locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            var success = false
            if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
               success = true
            }
            return success
        }catch (ex: Exception){

        }
        return false
    }

    private fun enableInternetAccess(context: Context, connectivityManager: ConnectivityManager?, wifi: WifiManager): AlertDialog.Builder{
        val builder: AlertDialog.Builder = AlertDialog.Builder(context)
        builder.setTitle(context.resources.getString(R.string.confirm))
        builder.setMessage(context.resources.getString(R.string.noInternetAccess))
        builder.setPositiveButton(context.resources.getString(R.string.access)){dialog, which ->
            wifi.setWifiEnabled(true)
            checkInternetAccess(context)
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

    private fun enableGPS(context: Context){
        val googleApiClient = GoogleApiClient.Builder(context)
            .addApi(LocationServices.API).build()
        googleApiClient.connect()
        val mLocationRequest: LocationRequest = LocationRequest.create()
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
        val builder =
            LocationSettingsRequest.Builder().addLocationRequest(mLocationRequest)
        builder.setAlwaysShow(true)
        val result: PendingResult<LocationSettingsResult> =
            LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build())
        result.setResultCallback(object : ResultCallback<LocationSettingsResult?> {
            override fun onResult(result: LocationSettingsResult) {
                val status: Status = result.status
                when (status.getStatusCode()) {
                    LocationSettingsStatusCodes.SUCCESS -> Log.i(
                        TAG,
                        "All location settings are satisfied."
                    )
                    LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> {
                        Log.i(
                            TAG,
                            "Location settings are not satisfied. Show the user a dialog to upgrade location settings "
                        )
                        try {
                            // Show the dialog by calling startResolutionForResult(), and check the result
                            // in onActivityResult().
                            status.startResolutionForResult(
                                context as Activity,
                                0x1
                            )
                        } catch (e: SendIntentException) {
                            Log.i(TAG, "PendingIntent unable to execute request.")
                        }
                    }
                    LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> Log.i(
                        TAG,
                        "Location settings are inadequate, and cannot be fixed here. Dialog not created."
                    )
                }
            }
        })
    }

    private fun noInternetAccess(context: Context, connectivityManager: ConnectivityManager?, wifi: WifiManager){
        createNotificationNoInternet(context)
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