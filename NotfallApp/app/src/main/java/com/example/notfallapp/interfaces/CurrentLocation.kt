package com.example.notfallapp.interfaces

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.AsyncTask
import android.os.Bundle
import androidx.core.app.ActivityCompat

/**
 * Class where we get our current Location back.
 */
class CurrentLocation {

    companion object: LocationListener{
        var gps: Boolean = true
        var currentLocation: Location? = null
        var context: Context? = null
        var locationManager: LocationManager? = null

        /**
         * Function which return current Location
         */
        fun getCurrentLocation(context: Context): Location?{
            CurrentLocation.context = context
            val lm = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            locationManager = lm

                if (lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    gps = true
                    getLastKnownLocation()
                } else if (lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                    gps = false
                    getLastKnownLocation()
                }

            return currentLocation
        }

        override fun onLocationChanged(p0: Location?) {

        }

        override fun onStatusChanged(p0: String?, p1: Int, p2: Bundle?) {

        }

        override fun onProviderEnabled(p0: String?) {

        }

        override fun onProviderDisabled(p0: String?) {

        }

        /**
         * Function who set the currentLocation to a variable which will be returned in getCurrentLocation
         */
        fun getLastKnownLocation() {
            class findLocation : AsyncTask<Unit, Unit, Unit>() {

                override fun doInBackground(vararg p0: Unit?) {
                    if (context?.let {
                            ActivityCompat.checkSelfPermission(
                                it,
                                Manifest.permission.ACCESS_FINE_LOCATION
                            )
                        } != PackageManager.PERMISSION_GRANTED && CurrentLocation!!.context?.let {
                            ActivityCompat.checkSelfPermission(
                                it,
                                Manifest.permission.ACCESS_COARSE_LOCATION
                            )
                        } != PackageManager.PERMISSION_GRANTED
                    ) {
                        //return null
                    }

                    val permissionGranted: Boolean = context?.let {
                        ActivityCompat.checkSelfPermission(
                            it, Manifest.permission.ACCESS_FINE_LOCATION)
                    } == PackageManager.PERMISSION_GRANTED

                    if (!permissionGranted){
                        try {
                        ActivityCompat.requestPermissions(context as Activity, Array(3) { Manifest.permission.ACCESS_FINE_LOCATION }, 200)
                        } catch (ex: RuntimeException) {
                            println("Error")
                        }
                    } else {
                        if (gps) {
                            locationManager?.removeUpdates(this@Companion)
                            locationManager?.requestLocationUpdates(
                                LocationManager.GPS_PROVIDER,
                                0,
                                200.0F,
                                this@Companion
                            )
                            currentLocation =
                                locationManager?.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                        } else {

                            locationManager?.requestLocationUpdates(
                                LocationManager.NETWORK_PROVIDER,
                                0,
                                200.0F,
                                this@Companion
                            )
                            currentLocation =
                                locationManager?.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
                        }
                    }
                }
            }
                val gd = findLocation()
                gd.execute()
        }
    }
}