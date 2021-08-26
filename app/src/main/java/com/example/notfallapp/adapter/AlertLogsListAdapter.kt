package com.example.notfallapp.adapter

import android.location.Location
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.notfallapp.R
import com.example.notfallapp.bll.AlertLog
import com.example.notfallapp.interfaces.CurrentLocation
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class AlertLogsListAdapter(private var alertLogs: MutableList<AlertLog>) : RecyclerView.Adapter<AlertLogsListAdapter.AlertLogsViewHolder>(){
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AlertLogsViewHolder {

        val itemView: View = LayoutInflater.from(parent.context).inflate(R.layout.recyclerview_item_alertlog, parent, false)
        return AlertLogsListAdapter.AlertLogsViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: AlertLogsViewHolder, position: Int) {
        val alertLog: AlertLog? = alertLogs[position]
        if(alertLog != null){
            holder.bindAlertLog(alertLog)
        }
    }

    fun getItems(): MutableList<AlertLog>{
        return alertLogs
    }

    override fun getItemCount(): Int {
        return alertLogs.size
    }

    class AlertLogsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        OnMapReadyCallback {
        private lateinit var tvSimpleAlertLogTime: TextView
        private lateinit var tvSimpleAlertLog: TextView
        private lateinit var flSimplemap: FrameLayout
        private lateinit var mapSimple: MapView
        private lateinit var mGoogleMap: GoogleMap

        fun bindAlertLog(alertLog: AlertLog) {
            tvSimpleAlertLogTime = itemView.findViewById(R.id.tvSimpleAlertLogTime)
            tvSimpleAlertLog = itemView.findViewById(R.id.tvSimpleAlertLog)

            when(alertLog.logType){
                0 -> tvSimpleAlertLog.text = itemView.context.getString(R.string.alertLog_AlertTriggered)
                1 -> tvSimpleAlertLog.text = itemView.context.getString(R.string.alertLog_AlertAccepted)
                2 -> tvSimpleAlertLog.text = itemView.context.getString(R.string.alertLog_AlertInvalidated)
                3 -> tvSimpleAlertLog.text = itemView.context.getString(R.string.alertLog_AlertClosed)
                4 -> tvSimpleAlertLog.text = itemView.context.getString(R.string.alertLog_CloseForcedBySystem)
                5 -> tvSimpleAlertLog.text = itemView.context.getString(R.string.alertLog_CloseForcedByMovementZone)
                6 -> tvSimpleAlertLog.text = itemView.context.getString(R.string.alertLog_CloseForcedByWlanConnection)
                10 -> tvSimpleAlertLog.text = itemView.context.getString(R.string.alertLog_UserContacted)
                11 -> tvSimpleAlertLog.text = itemView.context.getString(R.string.alertLog_UserStatusRecallDeprecated)
                12 -> tvSimpleAlertLog.text = itemView.context.getString(R.string.alertLog_UserContactedViaSms)
                13 -> tvSimpleAlertLog.text = itemView.context.getString(R.string.alertLog_UserContactedViaEmail)
                14 -> tvSimpleAlertLog.text = itemView.context.getString(R.string.alertLog_CallCenterContacted)
                20 -> tvSimpleAlertLog.text = itemView.context.getString(R.string.alertLog_UserNotReachable)
                21 -> tvSimpleAlertLog.text = itemView.context.getString(R.string.alertLog_UserAbsent)
                22 -> tvSimpleAlertLog.text = itemView.context.getString(R.string.alertLog_UserInactive)
                23 -> tvSimpleAlertLog.text = itemView.context.getString(R.string.alertLog_UserPhoneNumberMissing)
                24 -> tvSimpleAlertLog.text = itemView.context.getString(R.string.alertLog_UserNoContactTypeActivated)
                25 -> tvSimpleAlertLog.text = itemView.context.getString(R.string.alertLog_UserAlreadyAlerted)
                30 -> tvSimpleAlertLog.text = itemView.context.getString(R.string.alertLog_UserRefused)
                31 -> tvSimpleAlertLog.text = itemView.context.getString(R.string.alertLog_UserHangUp)
                40 -> tvSimpleAlertLog.text = itemView.context.getString(R.string.alertLog_alertRestartChain)
                41 -> tvSimpleAlertLog.text = itemView.context.getString(R.string.alertLog_alertingChainCompleted)
                50 -> {
                    if(alertLog.message != null){
                        tvSimpleAlertLog.text = alertLog.message
                    }
                }
                51 -> {
                    flSimplemap = itemView.findViewById(R.id.flSimpleMap)
                    val params = flSimplemap.layoutParams
                    params.height = 600
                    flSimplemap.layoutParams = params

                    mapSimple = itemView.findViewById(R.id.mapSimple)
                    mapSimple.onCreate(null)
                    mapSimple.onResume()
                    mapSimple.getMapAsync(this)

                    tvSimpleAlertLog.text = alertLog.message
                }
            }

            val timestamp = alertLog.date.split('.')[0]
            var time = timestamp.split('T')
            //val date = time[0].split('-')
            time = time[1].split(':')
            tvSimpleAlertLogTime.text = "${time[0]}:${time[1]}"
        }

        override fun onMapReady(googleMap: GoogleMap) {
            MapsInitializer.initialize(itemView.context)
            mGoogleMap = googleMap
            googleMap.mapType = GoogleMap.MAP_TYPE_NORMAL

            // add current location on the map, update itself
            /*if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (ContextCompat.checkSelfPermission(itemView.context,
                        Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                    googleMap.isMyLocationEnabled = true
                }
            } else {
                googleMap.isMyLocationEnabled = true
            }*/

            val location: Location = CurrentLocation.currentLocation!!

            // other way to mark the location with a marker, do not update itself
            googleMap.addMarker(MarkerOptions().position(LatLng(
                location.latitude,
                location.longitude
            )))

            // move the map to the position provided
            val liberty: CameraPosition = CameraPosition.builder().target(
                LatLng(
                location.latitude,
                location.longitude
            )
            ).zoom(
                14F
            ).bearing(0F).tilt(0F).build()
            googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(liberty))
        }
    }
}