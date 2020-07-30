package com.example.notfallapp.interfaces

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.AsyncTask
import android.os.Bundle
import androidx.core.app.ActivityCompat
import com.example.notfallapp.database.EmergencyAppDatabase

class CurrentLocation {

    companion object: LocationListener{
        var gps: Boolean = true
        var currentLocation: Location? = null
        var context: Context? = null
        var locationManager: LocationManager? = null
        fun getCurrentLocation(context: Context): Location?{
            CurrentLocation.context = context
            val lm = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            locationManager = lm

            var location: Location? = null

            if(lm.isProviderEnabled( LocationManager.GPS_PROVIDER ) ){
                gps = true
                getLastKnownLocation()
                location = currentLocation
            }else if( lm.isProviderEnabled( LocationManager.NETWORK_PROVIDER )) {
                gps = false
                getLastKnownLocation()
                location = currentLocation
            }

            return location
        }

        override fun onLocationChanged(p0: Location?) {

        }

        override fun onStatusChanged(p0: String?, p1: Int, p2: Bundle?) {

        }

        override fun onProviderEnabled(p0: String?) {

        }

        override fun onProviderDisabled(p0: String?) {

        }

        fun getLastKnownLocation() {
            class findLocation : AsyncTask<Unit, Unit, Unit>() {

                override fun doInBackground(vararg p0: Unit?) {
                    if (CurrentLocation.context?.let {
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
                    if(gps == true){
                        locationManager?.removeUpdates(this@Companion)
                        locationManager?.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 200.0F, this@Companion)
                        currentLocation = locationManager?.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                        println("Location: "+currentLocation.toString())
                    }else{

                        locationManager?.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 200.0F, this@Companion)
                        currentLocation= locationManager?.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
                    }
                }
            }

            val gd = findLocation()
            gd.execute()
        }
    }
}