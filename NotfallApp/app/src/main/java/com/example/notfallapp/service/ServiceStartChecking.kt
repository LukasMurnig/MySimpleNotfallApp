package com.example.notfallapp.service

import android.app.Activity
import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.net.ConnectivityManager
import android.os.IBinder
import com.example.notfallapp.MainActivity
import com.example.notfallapp.R
import com.example.notfallapp.interfaces.ICheckPermission
import com.example.notfallapp.interfaces.IConnectBracelet
import com.example.notfallapp.interfaces.INotifications
import java.util.*

class ServiceStartChecking: Service(), ICheckPermission, INotifications {
    companion object {
        var countGPS = 0
        var countInternet = 0
        var countBracelet = 0
    }
    override fun onBind(intent: Intent?): IBinder? {
        TODO("Not yet implemented")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        checkEnabled()
        return super.onStartCommand(intent, flags, startId)
    }

    fun checkEnabled(){
        var timer = Timer()
        timer.scheduleAtFixedRate(object : TimerTask()
        {
            override fun run() {
                var gps = isGPSEnabled(applicationContext)
                if(!gps){
                    if( countGPS == 0) {
                        createNotificationNoGPS(applicationContext)
                        countGPS++
                    }
                }else{
                    countGPS = 0
                }
                val connectivityManager =
                    applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
                var internet = isNetworkAvailable(connectivityManager)
                if(!internet){
                    if(countInternet == 0){
                        createNotificationNoInternet(applicationContext)
                        countInternet++
                    }
                }else{
                    countInternet = 0
                }
                if(IConnectBracelet.wasConnected) {
                    var connected = IConnectBracelet.connected
                    if (!connected) {
                        var mp = MediaPlayer.create(applicationContext, R.raw.connectionlost)
                        mp.start()
                        if (countBracelet == 0) {
                            createNotificationConnectionBraceletLost(applicationContext)
                            countBracelet++
                        }
                    } else {
                        countBracelet = 0
                    }
                }
            }

        },0, 5000)
    }
}