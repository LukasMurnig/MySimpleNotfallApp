package com.example.notfallapp.interfaces

import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.RemoteException
import android.util.Log
import androidx.fragment.app.Fragment
import org.altbeacon.beacon.*
import java.lang.Exception
import java.util.*
import kotlin.collections.ArrayList


/**
 * Class which look for Beacons in a region
 */
class BeaconInRange : Fragment(), BeaconConsumer{

    companion object{
        var beacons: ArrayList<Beacon?> = ArrayList()
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

    /**
     * Function should look for the first beacon it founds.
     */
    fun getBeacon(context: Context){
        var region = Region("Beacons", null, null, null)
        beaconManager = BeaconManager.getInstanceForApplication(context)!!
        beaconManager.beaconParsers.clear()
        beaconManager.beaconParsers.add(BeaconParser().setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24"))
        beaconManager?.bind(this)
        beaconManager?.addMonitorNotifier(object : MonitorNotifier {
            override fun didDetermineStateForRegion(p0: Int, region: Region?) {
                try {
                    beaconManager!!.startRangingBeaconsInRegion(region!!)

                } catch (e: RemoteException) {
                    e.printStackTrace()
                }
            }

            override fun didEnterRegion(region: Region?) {
                try {
                    beaconManager!!.startRangingBeaconsInRegion(region!!)

                } catch (e: RemoteException) {
                    e.printStackTrace()
                }
            }

            override fun didExitRegion(region: Region?) {
                try {
                    beaconManager!!.stopRangingBeaconsInRegion(region!!)
                } catch (e: RemoteException) {
                    e.printStackTrace()
                }
            }

        })

        beaconManager?.addRangeNotifier(object : RangeNotifier {
            override fun didRangeBeaconsInRegion(beaconsCollection: MutableCollection<Beacon>?, p1: Region?) {
                if (beaconsCollection!!.isNotEmpty()) {
                        for (indx in beaconsCollection.indices) {
                            if(beacons.size < 5) {
                                Log.e("Size", beacons.size.toString())
                                Log.e("BEACON", beaconsCollection.elementAt(indx).toString())
                                beacons.add(beaconsCollection.elementAt(indx))
                            }else{
                                    beaconManager!!.stopRangingBeaconsInRegion(region!!)
                                }
                        }
                }
                try {
                    var timer = Timer()
                    timer.schedule(object : TimerTask() {
                        override fun run() {
                            beaconManager!!.stopRangingBeaconsInRegion(region!!)
                        }
                    }, 0, 30000)
                }catch(ex: Exception){
                    Log.e("TimerException", ex.toString())
                }
            }

        })
        try {
            beaconManager!!.startMonitoringBeaconsInRegion(region)
        } catch (e: RemoteException) {
        }
    }
    override fun onDestroy() {
        super.onDestroy()
        beaconManager.unbind(this)
    }
}