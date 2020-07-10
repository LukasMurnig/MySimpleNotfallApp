package com.example.notfallapp.menubar

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.notfallapp.R
import com.example.notfallapp.interfaces.ICreatingOnClickListener

class SettingsActivity : AppCompatActivity(), ICreatingOnClickListener {

    private lateinit var btnSos: Button
    private lateinit var btnHome: ImageButton
    private lateinit var btnContact: ImageButton
    private lateinit var btnAlarms: ImageButton
    private lateinit var btnSettings: ImageButton

    private lateinit var tvName: TextView
    private lateinit var tvTelNr: TextView
    private lateinit var tvEmail: TextView
    private lateinit var btnChangeDate: Button
    private lateinit var spStartAppSettings: Spinner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        configureButtons()

        tvName = findViewById(R.id.tvName)
        tvTelNr = findViewById(R.id.tvTelNr)
        tvEmail = findViewById(R.id.tvEmail)
        btnChangeDate = findViewById(R.id.btnChangeData)
        spStartAppSettings = findViewById(R.id.spinnerAppStart)

        tvName.text = "Maria Musterfrau"
        tvTelNr.text = "0123456789"
        tvEmail.text = "maria.musterfrau@mail.com"

        btnChangeDate.setOnClickListener{
            // TODO implement change Profil Activity

            throw NotImplementedError()

            /*val intent = Intent(this, ChangeProfilActivity::class.java)
            intent.putExtra("name", tvName.text as String)
            intent.putExtra("telNr", tvTelNr.text as String)
            intent.putExtra("email", tvEmail.text as String)
            startActivityForResult(intent, Activity.RESULT_OK)*/
        }

        spStartAppSettings.adapter = ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,
            arrayListOf("App im Hintergrund geöffnet lassen", "App nur beim Starten öffnen"))
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