package com.example.notfallapp.interfaces

import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import androidx.core.content.ContextCompat.startActivity
import com.example.notfallapp.Login.LoginActivity
import com.example.notfallapp.MainActivity
import com.example.notfallapp.R
import com.example.notfallapp.menubar.contact.ContactActivity
import com.example.notfallapp.menubar.AlarmsActivity
import com.example.notfallapp.menubar.settings.SettingsActivity
import com.example.notfallapp.service.ServiceCallAlarm

interface ICreatingOnClickListener {

    fun createOnClickListener(context: Context,btnSos: Button,  btnHome: ImageButton, btnAlarms: ImageButton, btnContact: ImageButton, btnSettings: ImageButton){

        btnSos.setOnClickListener{

            // Service open Call Alarm Activity
            val intent = Intent(context, ServiceCallAlarm::class.java)
            context.startService(intent)
        }

        btnHome.setOnClickListener{
            val intent = Intent(context, MainActivity::class.java)
            startActivity(context, intent, null)
        }

        btnAlarms.setOnClickListener{
            val intent = Intent(context, AlarmsActivity::class.java)
            startActivity(context, intent, null)
        }

        btnContact.setOnClickListener(){
            val intent = Intent(context, ContactActivity::class.java)
            startActivity(context, intent, null)
        }

        btnSettings.setOnClickListener(){
            val intent = Intent(context, SettingsActivity::class.java)
            //val intent = Intent(context, LoginActivity::class.java)
            startActivity(context, intent, null)
        }
    }
}