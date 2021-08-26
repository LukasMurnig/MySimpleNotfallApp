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
import com.example.notfallapp.interfaces.ICheckPermission
import com.example.notfallapp.interfaces.ICreatingOnClickListener
import com.example.notfallapp.interfaces.INotifications
import com.example.notfallapp.menubar.alert.AlarmsActivity
import com.example.notfallapp.menubar.contact.ContactActivity
import com.example.notfallapp.menubar.settings.SettingsActivity
import com.example.notfallapp.service.ForegroundServiceCreateSOSButton
import com.example.notfallapp.service.ServiceCallAlarm

/**
 * Activity shows that the alarm was canceled
 **/
class AlarmCanceledActivity : AppCompatActivity(), ICreatingOnClickListener, INotifications,
    ICheckPermission {

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
        initComponents()

        btnCancelAlarmOk.setOnClickListener {
            Log.d(resources.getString(R.string.buttonOk),
                String.format(resources.getString(R.string.buttonOkClicked),
                              resources.getString(R.string.AlarmCanceled)))
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }

    /**
     * Initialize the components of the View
     */
    private fun initComponents() {
        configureButtons()
        btnCancelAlarmOk = findViewById(R.id.btn_cancel_alarm_ok)
        tvCanceledAlarm = findViewById(R.id.tvCanceledAlarm)
        checkInternetGPSPermissions(this)
    }

    /**
     * Configuration for our MenuBar und SOS Button
     */
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

    /**
     * override function, because when user clicked on the menubar the notification with the button sos must be shown again
     */
    override fun createOnClickListener(
        context: Context,
        btnSos: Button,
        btnHome: ImageButton,
        btnAlarms: ImageButton,
        btnContact: ImageButton,
        btnSettings: ImageButton
    ) {
        btnSos.setOnClickListener{
            Log.d(resources.getString(R.string.SOSButton),
                  String.format(resources.getString(R.string.SOSButtonClicked),
                                resources.getString(R.string.AlarmCanceled)))
            // Service opens Call Alarm Activity
            val intent = Intent(context, ServiceCallAlarm::class.java)
            context.startService(intent)
        }

        btnHome.setOnClickListener{
            Log.d(resources.getString(R.string.MenuItem),
                  String.format(resources.getString(R.string.HomeRegister),
                                resources.getString(R.string.AlarmCanceled)))
            startForegroundService(context, MainActivity::class.java)
        }

        btnAlarms.setOnClickListener{
            Log.d(resources.getString(R.string.MenuItem),
                  String.format(resources.getString(R.string.AlarmRegister),
                                resources.getString(R.string.AlarmCanceled)))
            startForegroundService(context, AlarmsActivity::class.java)
        }

        btnContact.setOnClickListener {
            Log.d(resources.getString(R.string.MenuItem),
                  String.format(resources.getString(R.string.ContactRegister),
                                resources.getString(R.string.AlarmCanceled)))
            startForegroundService(context, ContactActivity::class.java)
        }

        btnSettings.setOnClickListener {
            Log.d(resources.getString(R.string.MenuItem),
                  String.format(resources.getString(R.string.SettingsRegister),
                                resources.getString(R.string.AlarmCanceled)))
            startForegroundService(context, SettingsActivity::class.java)
        }
    }

    /**
     * Start Foreground Service to Stop an Alarm in the notificationBar
     */
    private fun startForegroundService(context: Context, activity: Class<*>){
        ForegroundServiceCreateSOSButton.startForegroundService(applicationContext)
        val intent = Intent(context, activity)
        ContextCompat.startActivity(context, intent, null)
    }
}