package com.example.notfallapp.interfaces

import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import androidx.core.content.ContextCompat.startActivity
import com.example.notfallapp.Login.SignUpActivity
import com.example.notfallapp.MainActivity
import com.example.notfallapp.menubar.AlarmsActivity
import com.example.notfallapp.menubar.ContactActivity

interface ICreatingOnClickListener {

    fun createOnClickListener(context: Context, btnSos: Button, btnHome: ImageButton, btnAlarms: ImageButton, btnContact: ImageButton, btnSettings: ImageButton){

        btnSos.setOnClickListener{
            // TODO Now open sign up, later send alert

            val intent = Intent(context, SignUpActivity::class.java)
            startActivity(context, intent,null)
        }

        btnHome.setOnClickListener{
            println("firstItem Click")

            Log.d("MenuItemClicked", "Homeregister were clicked in MainActivity")
            val intent = Intent(context, MainActivity::class.java)
            startActivity(context, intent, null)
        }

        btnAlarms.setOnClickListener{
            println("secondItem Click")
            // TODO Open register with alarms from Database
            Log.d("MenuItemClicked", "Alarmregister were clicked in MainActivity")
            val intent = Intent(context, AlarmsActivity::class.java)
            startActivity(context, intent, null)
        }

        btnContact.setOnClickListener{
            // TODO open ContactActivity
            Log.d("MenuItemClicked", "Contacts were clicked in MainActivity")
            val intent = Intent(context, ContactActivity::class.java)
            startActivity(context, intent, null)
        }

        btnSettings.setOnClickListener{
            // TODO open settingsActivity
            Log.d("MenuItemClicked", "Settings were clicked in MainActivity")
        }
    }
}