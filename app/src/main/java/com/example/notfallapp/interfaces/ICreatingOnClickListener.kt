package com.example.notfallapp.interfaces

import android.content.Context
import android.content.Intent
import android.widget.Button
import android.widget.ImageButton
import androidx.core.content.ContextCompat.startActivity
import com.example.notfallapp.MainActivity
import com.example.notfallapp.menubar.alert.AlarmsActivity
import com.example.notfallapp.menubar.contact.ContactActivity
import com.example.notfallapp.menubar.settings.SettingsActivity
import com.example.notfallapp.service.ServiceCallAlarm

/**
 * interface make it easier to create the menubar and the SOS button on the most Activities
 */
interface ICreatingOnClickListener {

    /**
     * creates the menubar and the SOS button on the most Activities
     */
    fun createOnClickListener(context: Context,btnSos: Button,  btnHome: ImageButton, btnAlarms: ImageButton, btnContact: ImageButton, btnSettings: ImageButton){

        btnSos.setOnClickListener {
            // Service opens Call Alarm Activity
            val intent = Intent(context, ServiceCallAlarm::class.java)
            context.startService(intent)
        }

        btnHome.setOnClickListener {
            val intent = Intent(context, MainActivity::class.java)
            startActivity(context, intent, null)
        }

        btnAlarms.setOnClickListener {
            val intent = Intent(context, AlarmsActivity::class.java)
            startActivity(context, intent, null)
        }

        btnContact.setOnClickListener {
            val intent = Intent(context, ContactActivity::class.java)
            startActivity(context, intent, null)
        }

        btnSettings.setOnClickListener {
            val intent = Intent(context, SettingsActivity::class.java)
            startActivity(context, intent, null)
        }
    }
}