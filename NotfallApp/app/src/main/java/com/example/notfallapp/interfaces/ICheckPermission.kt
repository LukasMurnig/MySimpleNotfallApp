package com.example.notfallapp.interfaces

import android.app.Activity
import android.app.AlertDialog
import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.content.IntentSender.SendIntentException
import android.location.LocationManager
import android.net.ConnectivityManager
import android.net.wifi.WifiInfo
import android.net.wifi.WifiManager
import android.os.Handler
import android.util.Log
import com.example.notfallapp.R
import com.example.notfallapp.server.ServerApi
import com.example.notfallapp.server.ServerApi.Companion.TAG
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.PendingResult
import com.google.android.gms.common.api.ResultCallback
import com.google.android.gms.common.api.Status
import com.google.android.gms.location.*
import java.util.*

/**
 * Interface which check if GPS, Internet and Bluetooth are enabled.
 */
interface ICheckPermission : INotifications {

    companion object{
        /**
         * Call refreshToken before it expires
         */
        fun getNewTokenBeforeExpires(expires: Long){
            val timer = Timer()

            timer.schedule(object : TimerTask() {
                override fun run() {
                    ServerApi.refreshToken()
                }
            },expires)
        }

        lateinit var wifiInfo: WifiInfo
        var level: Int? = null
    }

    /**
     * Function who check for all 3 Permissions
     */
    fun checkPermissions(context: Context){
        checkInternetAccess(context)
        checkBluetoothEnabled(context)
        checkGPSEnabled(context)
    }

    /**
     * Function which just check if Internet and GPs are enabled.
     */
    fun checkInternetGPSPermissions(context: Context){
        checkInternetAccess(context)
        checkGPSEnabled(context)
    }

    /**
     * Function which check if Internet is enabled and that you have a connection otherwise it asks for the permission
     */
    fun checkInternetAccess(context: Context){
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val wifi =
            context.getSystemService(Context.WIFI_SERVICE) as WifiManager
        wifiInfo = wifi.connectionInfo
        val numberOfLevels = 5
        val wifiInfo = wifi.connectionInfo
        level = WifiManager.calculateSignalLevel(wifiInfo.rssi, numberOfLevels)

        var builder: AlertDialog.Builder = AlertDialog.Builder(context)
        val handler = Handler(context.mainLooper)

        val timer = Timer()
        timer.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                val result = isNetworkAvailable(connectivityManager)
                if (!result){
                    timer.cancel()
                    handler.post( Runnable {
                        builder = enableInternetAccess(context, connectivityManager, wifi)
                        showAccess(builder)
                    })
                }
            }
        }, 0, 3000)
    }

    /**
     * Function which checks if Bluetooth is Enabled otherwise it asks for the permission
     */
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

    /**
     * Function which check if GPS is enabled otherwise it ask for the permission.
     */
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

    /**
     * Function check if Network is available
     */
    fun isNetworkAvailable(connectivityManager: ConnectivityManager?): Boolean {
        val activeNetworkInfo = connectivityManager!!.activeNetworkInfo
        return activeNetworkInfo != null && activeNetworkInfo.isConnected
    }

    /**
     * Function check if Bluetooth is enabled
     */
    fun isBluetoothEnabled(bluetoothAdapter: BluetoothAdapter): Boolean {
        var success = false
        if (bluetoothAdapter.isEnabled) {
            success = true
        }
        return success
    }

    /**
     * Function checks if GPS is enabled.
     */
    fun isGPSEnabled(context: Context):Boolean {
        try{
            val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            var success = false
            if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
               success = true
            }
            return success
        }catch (ex: Exception){

        }
        return false
    }

    /**
     * Dialog to Enable Internet.
     */
    private fun enableInternetAccess(context: Context, connectivityManager: ConnectivityManager?, wifi: WifiManager): AlertDialog.Builder{
        val builder: AlertDialog.Builder = AlertDialog.Builder(context)
        builder.setTitle(context.resources.getString(R.string.confirm))
        builder.setMessage(context.resources.getString(R.string.noInternetAccess))
        builder.setPositiveButton(context.resources.getString(R.string.access)){dialog, which ->
            wifi.isWifiEnabled = true
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

    /**
     * Dialog to enable Bluetooth
     */
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

    /**
     * Dialog to enable GPS
     */
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
                when (status.statusCode) {
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

    /**
     * Function to show Dialog again if them denies to enable Internet
     */
    private fun noInternetAccess(context: Context, connectivityManager: ConnectivityManager?, wifi: WifiManager){
        createNotificationNoInternet(context)
        val builder: AlertDialog.Builder = enableInternetAccess(context, connectivityManager, wifi)
        showAccess(builder)
    }
    /**
     * Function to show Dialog again if them denies to enable Bluetooth
     */
    private fun noBluetoothEnabled(context: Context, bluetoothAdapter: BluetoothAdapter){
        val builder: AlertDialog.Builder = enableBluetooth(context, bluetoothAdapter)
        showAccess(builder)
    }

    /**
     * Function which invoke Dialog and show it on the activity
     */
    private fun showAccess(builder: AlertDialog.Builder){
        val alertDialog: AlertDialog = builder.create()
        alertDialog.show()
    }

}