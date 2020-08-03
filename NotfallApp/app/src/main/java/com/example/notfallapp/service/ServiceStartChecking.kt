package com.example.notfallapp.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.example.notfallapp.MainActivity
import com.example.notfallapp.interfaces.ICheckPermission

class ServiceStartChecking: Service(), ICheckPermission {
    override fun onBind(intent: Intent?): IBinder? {
        TODO("Not yet implemented")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        MainActivity.context?.let { checkInternetGPSPermissions(it) }
        return super.onStartCommand(intent, flags, startId)
    }
}