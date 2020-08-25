package com.example.notfallapp.BroadcastReciever

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.notfallapp.alarm.AlarmSuccessfulActivity
import com.example.notfallapp.server.ServerApi.Companion.TAG
import com.example.notfallapp.service.ServiceCallAlarm
import com.example.notfallappLibrary.interfaces.VALRTIBracelet
import java.util.*

class ActionsBracelet : BroadcastReceiver(), VALRTIBracelet {
    companion object{
        var connected = false
        var batteryState = 0
    }

    private var context: Context? = null
    override fun onReceive(p0: Context?, p1: Intent?) {
        context = p0
        val action = p1?.action
        checkBatteryState()

        when (action){
            "ACTION_GATT_CONNECTED" -> connected = true
            "ACTION_GATT_DISCONNECTED" -> connected = false
            "Alarm called" -> callAlarm()
            "BatteryState" -> try{batteryState = valrtGetSelectedDeviceBattery(context!!).toInt()}catch(ex: Exception){ex.toString()}
        }
    }
    private fun callAlarm(){
        if(!AlarmSuccessfulActivity.isActive){
            val intent = Intent(context!!, ServiceCallAlarm::class.java)
            context!!.startService(intent)
        }
    }

    private fun checkBatteryState(){
        val timer = Timer()
        timer.schedule(object : TimerTask(){
            override fun run() {
                try{batteryState = valrtGetSelectedDeviceBattery(context!!).toInt()}catch(ex: Exception){ex.toString()}
            }
        }, 0, 5000)
    }
}