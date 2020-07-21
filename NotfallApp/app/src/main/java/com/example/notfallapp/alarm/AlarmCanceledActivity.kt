package com.example.notfallapp.alarm

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.notfallapp.MainActivity
import com.example.notfallapp.R
import com.example.notfallapp.interfaces.ICreatingOnClickListener
import com.example.notfallapp.interfaces.INotifications
import com.example.notfallapp.menubar.AlarmsActivity
import com.example.notfallapp.menubar.contact.ContactActivity
import com.example.notfallapp.menubar.settings.SettingsActivity
import com.example.notfallapp.service.ServiceCallAlarm

class AlarmCanceledActivity : AppCompatActivity(), ICreatingOnClickListener, INotifications {

    private lateinit var btnSos: Button
    private lateinit var btnHome: ImageButton
    private lateinit var btnContact: ImageButton
    private lateinit var btnAlarms: ImageButton
    private lateinit var btnSettings: ImageButton

    private lateinit var btnCancelAlarmOk: Button
    private lateinit var tvCanceledAlarm: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_call_alarm_canceled)
        configureButtons()
        initComponents()

        btnCancelAlarmOk.setOnClickListener {
            Log.d("ButtonOk", "Button ok in AlarmCanceledActivity clicked!")
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }

    private fun initComponents() {
        btnCancelAlarmOk = findViewById(R.id.btn_cancel_alarm_ok)
        tvCanceledAlarm = findViewById(R.id.tvCanceledAlarm)
    }

    private fun configureButtons() {
        // SOS Button
        btnSos = findViewById(R.id.btn_sos)

        // Button bar
        btnHome = findViewById(R.id.btnHome)
        btnAlarms = findViewById(R.id.btnAlarms)
        btnContact = findViewById(R.id.btnContact)
        btnSettings = findViewById(R.id.btnSettings)

        createOnClickListener(this, btnSos, btnHome, btnAlarms, btnContact, btnSettings)

    }

    override fun createOnClickListener(
        context: Context,
        btnSos: Button,
        btnHome: ImageButton,
        btnAlarms: ImageButton,
        btnContact: ImageButton,
        btnSettings: ImageButton
    ) {
        btnSos.setOnClickListener{
            Log.d("SOSButtonClicked", "SOS Button were clicked!")

            // Service open Call Alarm Activity
            val intent = Intent(context, ServiceCallAlarm::class.java)
            context.startService(intent)
        }

        btnHome.setOnClickListener{
            Log.d("MenuItemClicked", "Homeregister were clicked in MainActivity")
            createNotificationCreateAlarm(context)
            val intent = Intent(context, MainActivity::class.java)
            ContextCompat.startActivity(context, intent, null)
        }

        btnAlarms.setOnClickListener{
            Log.d("MenuItemClicked", "Alarmregister were clicked in MainActivity")
            createNotificationCreateAlarm(context)
            val intent = Intent(context, AlarmsActivity::class.java)
            ContextCompat.startActivity(context, intent, null)
        }

        btnContact.setOnClickListener(){
            Log.d("MenuItemClicked", "Contacts were clicked")
            createNotificationCreateAlarm(context)
            val intent = Intent(context, ContactActivity::class.java)
            ContextCompat.startActivity(context, intent, null)
        }

        btnSettings.setOnClickListener(){
            Log.d("MenuItemClicked", "Settings were clicked")
            createNotificationCreateAlarm(context)
            val intent = Intent(context, SettingsActivity::class.java)
            ContextCompat.startActivity(context, intent, null)
        }
    }
}