package com.example.notfallapp.menubar

import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import com.example.notfallapp.R
import com.example.notfallapp.interfaces.ICreatingOnClickListener

class AlarmsActivity : AppCompatActivity(), ICreatingOnClickListener {

    private lateinit var btnSos: Button
    private lateinit var btnHome: ImageButton
    private lateinit var btnContact: ImageButton
    private lateinit var btnAlarms: ImageButton
    private lateinit var btnSettings: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alarms)

        configureButtons()
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
}