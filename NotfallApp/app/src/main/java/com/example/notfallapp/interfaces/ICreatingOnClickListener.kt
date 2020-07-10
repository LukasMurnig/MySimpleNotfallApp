package com.example.notfallapp.interfaces

import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.ImageButton
import androidx.core.content.ContextCompat.startActivity
import com.example.notfallapp.MainActivity
import com.example.notfallapp.menubar.contact.ContactActivity

interface ICreatingOnClickListener {

    fun createOnClickListener(context: Context, btnHome: ImageButton, btnAlarms: ImageButton, btnContact: ImageButton, btnSettings: ImageButton){

        btnHome.setOnClickListener(){
            Log.d("MenuItemClicked", "Alarmregister were clicked")
            val intent = Intent(context, MainActivity::class.java)
            startActivity(context, intent, null)
        }

        btnAlarms.setOnClickListener(){
            println("secondItem Click")
            // TODO Open register with alarms from Database
            Log.d("MenuItemClicked", "Alarmregister were clicked")
        }

        btnContact.setOnClickListener(){
            // TODO open ContactActivity
            Log.d("MenuItemClicked", "Contacts were clicked")
            val intent = Intent(context, ContactActivity::class.java)
            startActivity(context, intent, null)
        }

        btnSettings.setOnClickListener(){
            // TODO open settingsActivity
            Log.d("MenuItemClicked", "Settings were clicked")
        }
    }
}