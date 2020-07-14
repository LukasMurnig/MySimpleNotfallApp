package com.example.notfallapp.menubar

import android.Manifest
import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Context
import android.content.pm.PackageManager
import android.location.*
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.notfallapp.R
import com.example.notfallapp.interfaces.ICreatingOnClickListener
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import java.io.IOException
import java.util.*


// Key: AIzaSyAQ3T7x_6I67x2EnA_PpO9yw3y8g_K8BlM
open class MapActivity : AppCompatActivity(),  OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
    GoogleApiClient.OnConnectionFailedListener,
    LocationListener, ICreatingOnClickListener {

    private lateinit var btnSos: Button
    private lateinit var btnHome: ImageButton
    private lateinit var btnContact: ImageButton
    private lateinit var btnAlarms: ImageButton
    private lateinit var btnMap: ImageButton
    private lateinit var btnSettings: ImageButton

    val MY_PERMISSIONS_REQUEST_LOCATION = 99
    lateinit var mGoogleApiClient: GoogleApiClient
    lateinit var mLastLocation: Location
    var mCurrLocationMarker: Marker? = null
    lateinit var mLocationRequest: LocationRequest
    private lateinit var mMap: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)

        configureButtons()

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkLocationPermission()
        }
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment?

        mapFragment!!.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        mMap.mapType = GoogleMap.MAP_TYPE_NORMAL
        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.uiSettings.isZoomGesturesEnabled = true
        mMap.uiSettings.isCompassEnabled = true

        //Initialize Google Play Services
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
                buildGoogleApiClient()
                mMap.isMyLocationEnabled = true
            }
        } else {
            buildGoogleApiClient()
            mMap.isMyLocationEnabled = true
        }
    }

    @Synchronized
    protected fun buildGoogleApiClient() {
        mGoogleApiClient = GoogleApiClient.Builder(this)
            .addConnectionCallbacks(this)
            .addOnConnectionFailedListener(this)
            .addApi(LocationServices.API)
            .build()
        mGoogleApiClient.connect()
    }

    //When the connect request has successfully completed
    @SuppressLint("RestrictedApi")
    override fun onConnected(bundle: Bundle?) {
        mLocationRequest = LocationRequest()
        mLocationRequest.interval = 30000
        mLocationRequest.fastestInterval = 10000
        mLocationRequest.priority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
            == PackageManager.PERMISSION_GRANTED
        ) {
            val pendingIntent: PendingIntent =PendingIntent.getService(this, 0, intent, 0)

            LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient,
                mLocationRequest, pendingIntent
            )
        }
    }

    //Called when the client is temporarily in a disconnected state.
    override fun onConnectionSuspended(i: Int) {

    }

    override fun onConnectionFailed(p0: ConnectionResult) {
        TODO("Not yet implemented")
    }

    override fun onLocationChanged(location: Location) {
        mLastLocation = location
        if (mCurrLocationMarker != null) {
            mCurrLocationMarker!!.remove()
        }
        //Showing Current Location Marker on Map
        //Showing Current Location Marker on Map
        val latLng = LatLng(location.latitude, location.longitude)
        val markerOptions = MarkerOptions()
        markerOptions.position(latLng)
        val locationManager =
            getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val provider = locationManager.getBestProvider(Criteria(), true)
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
            != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        val locations = locationManager.getLastKnownLocation(provider)
        val providerList =
            locationManager.allProviders
        if (null != locations && null != providerList && providerList.size > 0) {
            val longitude = locations.longitude
            val latitude = locations.latitude
            val geocoder = Geocoder(
                applicationContext,
                Locale.getDefault()
            )
            try {
                val listAddresses: List<Address>? = geocoder.getFromLocation(
                    latitude,
                    longitude, 1
                )
                if (null != listAddresses && listAddresses.size > 0) {
                    val state: String = listAddresses[0].getAdminArea()
                    val country: String = listAddresses[0].getCountryName()
                    val subLocality: String = listAddresses[0].getSubLocality()
                    markerOptions.title(
                        "" + latLng + "," + subLocality + "," + state
                                + "," + country
                    )
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
        mCurrLocationMarker = mMap.addMarker(markerOptions)
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng))
        mMap.animateCamera(CameraUpdateFactory.zoomTo(11f))
        val pendingIntent: PendingIntent = PendingIntent.getService(this, 0, intent, 0)
        LocationServices.FusedLocationApi.removeLocationUpdates(
            mGoogleApiClient,
            pendingIntent
        )
    }

    private fun checkLocationPermission(): Boolean {
        return if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
            != PackageManager.PERMISSION_GRANTED
        ) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            ) {
                ActivityCompat.requestPermissions(
                    this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    MY_PERMISSIONS_REQUEST_LOCATION
                )
            } else {
                ActivityCompat.requestPermissions(
                    this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    MY_PERMISSIONS_REQUEST_LOCATION
                )
            }
            false
        } else {
            true
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            MY_PERMISSIONS_REQUEST_LOCATION -> {
                if (grantResults.size > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED
                ) {
                    if (ContextCompat.checkSelfPermission(
                            this,
                            Manifest.permission.ACCESS_FINE_LOCATION
                        )
                        == PackageManager.PERMISSION_GRANTED
                    ) {
                        mMap.isMyLocationEnabled = true
                    }
                } else {
                    Toast.makeText(
                        this, "permission denied",
                        Toast.LENGTH_LONG
                    ).show()
                }
                return
            }
        }
    }

    override fun onStatusChanged(p0: String?, p1: Int, p2: Bundle?) {
        TODO("Not yet implemented")
    }

    override fun onProviderEnabled(p0: String?) {
        TODO("Not yet implemented")
    }

    override fun onProviderDisabled(p0: String?) {
        TODO("Not yet implemented")
    }

    private fun configureButtons() {
        // SOS Button
        btnSos = findViewById(R.id.btn_sos)

        // Button bar
        btnHome = findViewById(R.id.btnHome)
        btnAlarms = findViewById(R.id.btnAlarms)
        btnContact = findViewById(R.id.btnContact)
        btnMap = findViewById(R.id.btnMap)
        btnSettings = findViewById(R.id.btnSettings)

        createOnClickListener(this, btnSos, btnHome, btnAlarms, btnContact, btnMap, btnSettings)
    }
}