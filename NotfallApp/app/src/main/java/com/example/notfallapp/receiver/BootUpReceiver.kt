package com.example.notfallapp.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.notfallapp.MainActivity
import com.example.notfallapp.interfaces.INotificationCreateAlarm

class BootUpReceiver : BroadcastReceiver() {

    // should open MainActivity, when device is finished with boot up
    override fun onReceive(context: Context?, intent: Intent?) {
        if (context != null && intent !=null){
            if (Intent.ACTION_BOOT_COMPLETED.equals(intent.action)) {
                val i = Intent(context, MainActivity::class.java)
                i.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                context.startActivity(i)
            }
        }
    }

}