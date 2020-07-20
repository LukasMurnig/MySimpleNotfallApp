package com.example.notfallapp.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.preference.PreferenceManager
import com.example.notfallapp.MainActivity
import com.example.notfallapp.R
import com.example.notfallapp.interfaces.INotificationCreateAlarm
import com.example.notfallapp.menubar.settings.SettingsActivity

class BootUpReceiver : BroadcastReceiver() {

    // should open MainActivity, when device is finished with boot up
    override fun onReceive(context: Context?, intent: Intent?) {
        if (context != null && intent !=null){
            val prefs = PreferenceManager.getDefaultSharedPreferences(context)

            if (Intent.ACTION_BOOT_COMPLETED == intent.action) {
                // ?
                if(prefs.getBoolean("switch", true)){
                    val i = Intent(context, MainActivity::class.java)
                    i.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    context.startActivity(i)
                }
            }
        }
    }

}