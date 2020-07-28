package com.example.notfallapp.interfaces

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import androidx.core.app.ActivityCompat

interface ICurrentLocation /*: LocationListener*/ {

    fun getCurrentLocation(context: Context): Location?{
        val lm = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return null
        }

        var location: Location? = null

        if( lm.isProviderEnabled( LocationManager.NETWORK_PROVIDER )) {
            //lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 100, 100.0F, this)
            location = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
        } else if(lm.isProviderEnabled( LocationManager.GPS_PROVIDER ) ){
            //lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 100, 100.0F, this)
            location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER)
        }

        return location
    }
}