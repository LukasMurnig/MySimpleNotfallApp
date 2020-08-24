package com.example.notfallapp.interfaces

import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.RemoteException
import android.util.Log
import androidx.fragment.app.Fragment
import org.altbeacon.beacon.*

class BeaconInRange : Fragment(), BeaconConsumer{

    companion object{
        var beacon: Beacon? = null
    }
    lateinit var beaconManager: BeaconManager

    override fun onBeaconServiceConnect() {}
    override fun getApplicationContext(): Context? {
        return activity!!.applicationContext
    }

    override fun unbindService(serviceConnection: ServiceConnection?) {
        activity!!.unbindService(serviceConnection!!)
    }

    override fun bindService(
        intent: Intent?,
        serviceConnection: ServiceConnection?,
        i: Int
    ): Boolean {
        beaconManager?.bind(this)
        return activity!!.bindService(intent, serviceConnection!!, i)
    }

    fun getBeacon(){
        var region = Region("Beacon", null, null, null)
        beaconManager = activity?.let { BeaconManager.getInstanceForApplication(it) }!!
        beaconManager?.bind(this)
        Log.e("Tag", "HELLO" )
        beaconManager?.addMonitorNotifier(object: MonitorNotifier {
            override fun didDetermineStateForRegion(p0: Int, region: Region?) {
                Log.e("GGF", "DEtermine")
            }

            override fun didEnterRegion(region: Region?) {
                Log.e("GDSAF", "EnterRegion")
                try {
                    beaconManager!!.startRangingBeaconsInRegion(region!!)

                } catch (e: RemoteException) {
                    e.printStackTrace()
                }
            }

            override fun didExitRegion(region: Region?) {
            Log.e("asdf","ExitRegion")
                try {
                    beaconManager!!.stopRangingBeaconsInRegion(region!!)
                } catch (e: RemoteException) {
                    e.printStackTrace()
                }
            }

        })

        beaconManager?.addRangeNotifier(object: RangeNotifier{
            override fun didRangeBeaconsInRegion(beacons: MutableCollection<Beacon>?, p1: Region?) {
                Log.e("TAG", beacons.toString())
                if(beacons!!.size > 0){
                    beacon=beacons.first()
                }
            }

        })
        try {
            beaconManager!!.startMonitoringBeaconsInRegion(region)
        } catch (e: RemoteException) {
        }
    }
}