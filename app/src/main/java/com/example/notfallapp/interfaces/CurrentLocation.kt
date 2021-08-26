package com.example.notfallapp.interfaces

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.os.Bundle
import android.os.Looper
import android.util.Log
import androidx.core.app.ActivityCompat
import com.example.notfallapp.alarm.AlarmSuccessfulActivity
import com.example.notfallapp.server.ServerBookOnOff
import com.example.notfallapp.server.ServerCallAlarm
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import com.google.android.gms.location.LocationRequest




/**
 * Class where we get our current Location back.
 */
class CurrentLocation {

    companion object: LocationListener{
        var currentLocation: Location? = null
        var context: Context? = null
        private lateinit var fusedLocationClient: FusedLocationProviderClient
        private lateinit var locationCallback: LocationCallback

        /**
         * Function which return current Location
         */
        fun getCurrentLocation(context: Context): Location?{
            CurrentLocation.context = context
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
            searchForLocation()
            return currentLocation
        }

        override fun onLocationChanged(p0: Location?) {
            currentLocation = p0
            if (AlarmSuccessfulActivity.isActive){
                // Send position to the Server
                context?.let { ServerCallAlarm.sendPosition(it) }
            }
        }

        override fun onStatusChanged(p0: String?, p1: Int, p2: Bundle?) {

        }

        override fun onProviderEnabled(p0: String?) {

        }

        override fun onProviderDisabled(p0: String?) {

        }

        fun searchForLocation() = runBlocking{
            val searchLocation = async { getLastKnownLocation() }
            searchLocation.await()
        }

        /**
         * Function who set the currentLocation to a variable which will be returned in getCurrentLocation
         */
        @SuppressLint("RestrictedApi")
        private fun getLastKnownLocation(){
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(context!!)
            context?.let {
                    ActivityCompat.checkSelfPermission(
                        it,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    )
                } != PackageManager.PERMISSION_GRANTED && context?.let {
                    ActivityCompat.checkSelfPermission(
                        it,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                } != PackageManager.PERMISSION_GRANTED
            val googleLocation = fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                currentLocation = location
                ServerBookOnOff.checkLocationGeoZone(context!!)
            }
            /*if (googleLocation.latitude == null){
                Log.e("Location", "lastLocation was null")
                setLocationCallback()
                val locationRequest = LocationRequest()
                locationRequest.fastestInterval = 5000
                locationRequest.interval = 10000
                locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
                fusedLocationClient.requestLocationUpdates(locationRequest,
                    locationCallback,
                    Looper.getMainLooper())
            }else currentLocation = googleLocation*/
        }

        /**
         * Location Callback for the location update callback
         */
        private fun setLocationCallback(){
            locationCallback = object : LocationCallback(){
                override fun onLocationResult(locationResult: LocationResult?) {
                    locationResult ?: return
                    for (location in locationResult.locations) currentLocation = location
                }
            }
        }
    }
}