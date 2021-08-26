package com.example.notfallapp.interfaces

import android.app.Activity
import android.app.Dialog
import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.content.IntentSender.SendIntentException
import android.location.LocationManager
import android.net.ConnectivityManager
import android.net.wifi.WifiInfo
import android.net.wifi.WifiManager
import android.os.Handler
import android.util.Log
import android.view.Window
import android.widget.Button
import android.widget.TextView
import com.example.notfallapp.R
import com.example.notfallapp.server.ServerApi
import com.example.notfallapp.server.ServerApi.Companion.TAG
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.PendingResult
import com.google.android.gms.common.api.Status
import com.google.android.gms.location.*
import java.util.*
import android.content.Intent
import android.provider.Settings
import com.example.notfallapp.MainActivity
import com.example.notfallapp.connectBracelet.AddBraceletActivity
import com.example.notfallapp.login.LoginActivity
import com.example.notfallapp.service.ForegroundServiceCreateSOSButton


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
        var internetDialog: Dialog? = null
        var bluetoothDialog: Dialog? = null
        var gpsDialog: Dialog? = null
        var internetDialogShown = false
        var bluetoothDialogShown = false
        var gpsDialogShown = false
        var level: Int? = null
    }

    /**
     * Function which check if bluetooth is Enabled so that we can look for beacons.
     */
    fun checkBluetoothPermission(context: Context){
        val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        val success = isBluetoothEnabled(bluetoothAdapter)
        if(!success){
            enableBluetoothForBeacon(context, bluetoothAdapter)
        }
        checkBluetoothEnabled(context)
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
        checkInternetEnabled(context, connectivityManager, wifi)
    }

    /**
     * Function which checks if Bluetooth is Enabled otherwise it asks for the permission
     */
    fun checkBluetoothEnabled(context: Context){

        val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        val handler = Handler(context.mainLooper)

        val timer = Timer()
        timer.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                val success = isBluetoothEnabled(bluetoothAdapter)
                if(!success){
                    timer.cancel()
                    handler.post{
                        enableBluetooth(context, bluetoothAdapter)
                    }
                    bluetoothDialog?.dismiss()
                    var addBraceletActivity = AddBraceletActivity()
                    addBraceletActivity.searchDevice()
                }else{
                    bluetoothDialog?.dismiss()
                    val intent = Intent(context, MainActivity::class.java)
                    context.startActivity(intent)
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
                    if(!gpsDialogShown) {
                        handler.post {
                            gpsDialogShown = true
                            //ForegroundServiceCreateSOSButton.sosButtonShown = false
                            //createNotificationNoGPS(context)
                            enableGPS(context)
                        }
                    }
                }else{
                    if(LoginActivity.loggedIn){
                        if (!ForegroundServiceCreateSOSButton.sosButtonShown) setNotificationBarToSOSButton(context)
                    }
                    gpsDialog?.dismiss()
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
    private fun enableInternetAccess(context: Context, connectivityManager: ConnectivityManager?, wifi: WifiManager){
        internetDialog = Dialog(context)
        internetDialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
        internetDialog!!.setCancelable(false)
        internetDialog!!.setContentView(R.layout.dialog)
        var textview: TextView = internetDialog!!.findViewById(R.id.text_dialog)
        textview.text = context.resources.getString(R.string.noInternetAccess)
        var positiveButton: Button = internetDialog!!.findViewById(R.id.btn_dialog_positive)
        var negativeButton: Button = internetDialog!!.findViewById(R.id.btn_dialog_negative)
        positiveButton.setOnClickListener {
            enableInternet(context)
            wifi.isWifiEnabled = true
            setNotificationBarToSOSButton(context)
            checkInternetAccess(context)
            internetDialog!!.dismiss()
        }
        negativeButton.setOnClickListener{
            val result = isNetworkAvailable(connectivityManager)
            if (!result) {
                noInternetAccess(context, connectivityManager, wifi)
            }
        }
        internetDialog!!.show()
        checkInternetEnabled(context, connectivityManager, wifi)
    }

    /**
     * Function checks in Timer if Internet is enabled.
     */
    private fun checkInternetEnabled(context: Context, connectivityManager: ConnectivityManager?, wifi: WifiManager){
        val handler = Handler(context.mainLooper)
        val timer = Timer()
        timer.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                val result = isNetworkAvailable(connectivityManager)
                if (!result){
                    if(!internetDialogShown){
                        timer.cancel()
                        handler.post {
                            internetDialogShown = true
                            enableInternetAccess(context, connectivityManager, wifi)
                        }
                    }
                }else{
                    if (LoginActivity.loggedIn){
                        if (!ForegroundServiceCreateSOSButton.sosButtonShown) setNotificationBarToSOSButton(context)
                    }
                    internetDialog?.dismiss()
                }
            }
        }, 0, 3000)
    }

    /**
     * Function which open the Device Settings,so that the user can enable mobile data.
     */
    private fun enableInternet(context: Context){
        val settingsIntent = Intent(Settings.ACTION_NETWORK_OPERATOR_SETTINGS)
        context.startActivity(settingsIntent)
    }

    /**
     * Dialog to enable Bluetooth
     */
    private fun enableBluetooth(context: Context, bluetoothAdapter: BluetoothAdapter){
        bluetoothDialog = Dialog(context)
        bluetoothDialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
        bluetoothDialog!!.setCancelable(false)
        bluetoothDialog!!.setContentView(R.layout.dialog)
        var textview: TextView = bluetoothDialog!!.findViewById(R.id.text_dialog)
        textview.text = context.resources.getString(R.string.noBluetooth)
        var positiveButton: Button = bluetoothDialog!!.findViewById(R.id.btn_dialog_positive)
        var negativeButton: Button = bluetoothDialog!!.findViewById(R.id.btn_dialog_negative)
        positiveButton.setOnClickListener {
            bluetoothAdapter.enable()
            checkBluetoothEnabled(context)
            bluetoothDialog!!.dismiss()
        }
        negativeButton.setOnClickListener{
            val success = isBluetoothEnabled(bluetoothAdapter)
            if (!success){
                noBluetoothEnabled(context, bluetoothAdapter)
            }
        }
        bluetoothDialog!!.show();
    }

    /**
     * Dialog to enable Bluetooth but just ones
     */

    private fun enableBluetoothForBeacon(context: Context, bluetoothAdapter: BluetoothAdapter){
        bluetoothDialog = Dialog(context)
        bluetoothDialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
        bluetoothDialog!!.setCancelable(false)
        bluetoothDialog!!.setContentView(R.layout.dialog)
        var textview: TextView = bluetoothDialog!!.findViewById(R.id.text_dialog)
        textview.text = context.resources.getString(R.string.hintMessage)
        var positiveButton: Button = bluetoothDialog!!.findViewById(R.id.btn_dialog_positive)
        var negativeButton: Button = bluetoothDialog!!.findViewById(R.id.btn_dialog_negative)
        positiveButton.setOnClickListener {
            bluetoothAdapter.enable()
            checkBluetoothEnabled(context)
            bluetoothDialog!!.dismiss()
        }
        negativeButton.setOnClickListener{
           bluetoothDialog?.dismiss()
        }
        bluetoothDialog!!.show();
    }

    /**
     * Dialog to enable GPS
     */
    private fun enableGPS(context: Context){
        gpsDialog = Dialog(context)
        gpsDialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
        gpsDialog!!.setCancelable(false)
        gpsDialog!!.setContentView(R.layout.dialog)
        var textview: TextView = gpsDialog!!.findViewById(R.id.text_dialog)
        textview.text = context.resources.getString(R.string.needGPS)
        var positiveButton: Button = gpsDialog!!.findViewById(R.id.btn_dialog_positive)
        var negativeButton: Button = gpsDialog!!.findViewById(R.id.btn_dialog_negative)
        positiveButton.setOnClickListener {
            checkGPSEnabled(context)
            showGoogleDialog(context)
            gpsDialog!!.dismiss()
        }
        negativeButton.setOnClickListener{
            val result = isGPSEnabled(context)
            if(!result) noGPSEnabled(context)
        }
        gpsDialog!!.show();
    }

    /**
     * Dialog from Google for enabling von GPS
     */
    private fun showGoogleDialog(context: Context){
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
        result.setResultCallback { result ->
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
                        setNotificationBarToSOSButton(context)
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
    }

    /**
     * Function to show Dialog again if them denies to enable Internet
     */
    private fun noInternetAccess(context: Context, connectivityManager: ConnectivityManager?, wifi: WifiManager){
        //ForegroundServiceCreateSOSButton.sosButtonShown = false
        //createNotificationNoInternet(context)
        enableInternetAccess(context, connectivityManager, wifi)
    }
    /**
     * Function to show Dialog again if them denies to enable Bluetooth
     */
    private fun noBluetoothEnabled(context: Context, bluetoothAdapter: BluetoothAdapter){
        enableBluetooth(context, bluetoothAdapter)
    }

    /**
     * Function to show Dialog again if the user denies to enable GPS
     */
    private fun noGPSEnabled(context: Context){
        enableGPS(context)
    }

    private fun  setNotificationBarToSOSButton(context: Context){
        // start the foregroundService which opens the notifcation with the SOS button
        ForegroundServiceCreateSOSButton.startForegroundService(context)
    }

}