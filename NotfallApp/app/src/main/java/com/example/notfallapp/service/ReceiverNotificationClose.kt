package com.example.notfallapp.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log

class ReceiverNotificationClose : BroadcastReceiver() {
    override fun onReceive(p0: Context?, p1: Intent?) {
        Log.i("Notification", "Service Stops")

        ForegroundServiceCreateSOSButton.startForegroundService(p0!!)
    }
}