package com.example.notfallapp.service

import android.app.Service
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.os.IBinder
import com.example.notfallapp.interfaces.ICheckPermission
import com.example.notfallapp.interfaces.INotifications
import java.util.*

/**
 * Service which check if gps, internet and connection to bracelet is on/active,
 * when not it will send a notification with the problem
 */
class ServiceStartChecking: Service(), ICheckPermission, INotifications {
    companion object {
        var countGPS = 0
        var countInternet = 0
        var countBracelet = 0
    }
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        checkEnabled()
        return super.onStartCommand(intent, flags, startId)
    }

    /**
     * check if gps, internet and connection to bracelet is on/active
     */
    private fun checkEnabled(){
        val timer = Timer()
        timer.scheduleAtFixedRate(object : TimerTask()
        {
            override fun run() {
                val gps = isGPSEnabled(applicationContext)
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
                val internet = isNetworkAvailable(connectivityManager)
                if(!internet){
                    if(countInternet == 0){
                        createNotificationNoInternet(applicationContext)
                        countInternet++
                    }
                }else{
                    countInternet = 0
                }
                /*if(IConnectBracelet.wasConnected) {
                    val connected = IConnectBracelet.connected
                    if (!connected) {
                        if (countBracelet == 0) {
                            createNotificationConnectionBraceletLost(applicationContext)
                            val mp = MediaPlayer.create(applicationContext, R.raw.connectionlost)
                            mp.start()
                            countBracelet++
                        }
                    } else {
                        countBracelet = 0
                    }
                }*/
            }

        },0, 5000)
    }
}