package com.example.notfallapp.menubar.alert

import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.notfallapp.R
import com.example.notfallapp.interfaces.ICreatingOnClickListener
import java.util.*

class DetailAlertActivity : AppCompatActivity(), ICreatingOnClickListener {

    private lateinit var btnSos: Button
    private lateinit var btnHome: ImageButton
    private lateinit var btnContact: ImageButton
    private lateinit var btnAlarms: ImageButton
    private lateinit var btnSettings: ImageButton

    private lateinit var tvDetailDeviceId: TextView
    private lateinit var tvDetailLongitude: TextView
    private lateinit var tvDetailLatitude: TextView
    private lateinit var tvDetailDate: TextView
    private lateinit var tvDetailTime: TextView
    private lateinit var tvDetailAlarmAccepted: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_alert)

        installComponents()

        val extras = intent.extras ?: return
        tvDetailDeviceId.text = (extras.get("deviceId") as UUID?).toString()

        tvDetailLongitude.text = extras.getDouble("longitude").toString()
        tvDetailLatitude.text = extras.getDouble("latitude").toString()

        val timestamp = extras.getString("timestamp")?.split('.')?.get(0)
        tvDetailDate.text = timestamp?.split('T')?.get(0)
        tvDetailTime.text = timestamp?.split('T')?.get(1)

        if((extras.get("accepted")) != null){
            tvDetailAlarmAccepted.text = (extras.get("accepted") as UUID?).toString()
        } else {
            tvDetailAlarmAccepted.text = resources.getText(R.string.AlarmAccepted)
        }
    }

    private fun installComponents(){
        configureButtons()

        tvDetailDeviceId = findViewById(R.id.tvDetailDeviceId)
        tvDetailLongitude = findViewById(R.id.tvDetailLongitude)
        tvDetailLatitude = findViewById(R.id.tvDetailLatitude)
        tvDetailDate = findViewById(R.id.tvDetailDate)
        tvDetailTime = findViewById(R.id.tvDetailTime)
        tvDetailAlarmAccepted = findViewById(R.id.tvDetailAlarmAccepted)
    }

    private fun configureButtons() {
        // SOS Button
        btnSos = findViewById(R.id.btn_sos)

        // Button bar
        btnHome = findViewById(R.id.btnHome)
        btnAlarms = findViewById(R.id.btnAlarms)
        //btnAlarms.setImageResource(R.drawable.device_active)
        btnContact = findViewById(R.id.btnContact)
        btnSettings = findViewById(R.id.btnSettings)
        createOnClickListener(this, btnSos, btnHome, btnAlarms, btnContact, btnSettings)
    }
}