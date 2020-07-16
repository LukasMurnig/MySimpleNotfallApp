package com.example.notfallapp.interfaces

import android.content.Context
import android.content.Intent
import android.provider.Settings
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import androidx.core.content.ContextCompat.startActivity
import com.example.notfallapp.MainActivity
import com.example.notfallapp.menubar.contact.ContactActivity
import androidx.room.Room
import com.example.notfallapp.bll.Alarm
import com.example.notfallapp.database.AlarmDatabase
import com.example.notfallapp.menubar.AlarmsActivity
import com.example.notfallapp.menubar.SettingsActivity
import com.example.notfallapp.service.ServiceCallAlarm
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*

interface ICreatingOnClickListener {

    fun createOnClickListener(context: Context,btnSos: Button,  btnHome: ImageButton, btnAlarms: ImageButton, btnContact: ImageButton, btnSettings: ImageButton){

        btnSos.setOnClickListener{
            Log.d("SOSButtonClicked", "SOS Button were clicked!")

            // Service open Call Alarm Activity
            val intent = Intent(context, ServiceCallAlarm::class.java)
            context.startService(intent)
        }

        btnHome.setOnClickListener{
            Log.d("MenuItemClicked", "Homeregister were clicked in MainActivity")
            val intent = Intent(context, MainActivity::class.java)
            startActivity(context, intent, null)
        }

        btnAlarms.setOnClickListener{
            // TODO Open register with alarms from Database
            Log.d("MenuItemClicked", "Alarmregister were clicked in MainActivity")
            val intent = Intent(context, AlarmsActivity::class.java)
            startActivity(context, intent, null)
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
            val intent = Intent(context, SettingsActivity::class.java)
            startActivity(context, intent, null)
        }
    }

    fun createAlarmInDb(context: Context){
        val android_id: String = Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
        val clickedTime: Date = Calendar.getInstance().time

        val alarm = Alarm(android_id, "IAmTest", clickedTime.toString())

        val db = Room.databaseBuilder(context, AlarmDatabase::class.java, "alarms.db").fallbackToDestructiveMigration().build()
        try{
            GlobalScope.launch {
                // zum Testen
                db.alarmsDao().deleteAll()

                db.alarmsDao().insertAlarm(alarm)
            }
        }catch (ex: Exception){
            println("Konnte Alarm nicht speichern. Grund: $ex")
        }
    }
}