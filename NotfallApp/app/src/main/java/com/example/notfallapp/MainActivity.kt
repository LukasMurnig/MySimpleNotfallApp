package com.example.notfallapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import com.example.notfallapp.Login.SignUpActivity
import com.example.notfallapp.menubar.ICreatingButtonBar

class MainActivity : AppCompatActivity(), ICreatingButtonBar {

    private lateinit var btnSos: Button
    private lateinit var btnHome: ImageButton
    private lateinit var btnContact: ImageButton
    private lateinit var btnAlarms: ImageButton
    private lateinit var btnSettings: ImageButton
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        createButtonBar()

        btnSos = findViewById(R.id.btn_sos)

        btnSos.setOnClickListener(){
            // TODO Now open sign up, later send alert
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }

       /* btnAlarms.setOnMenuItemClickListener(){
            // TODO Open register with alarms from Database
        }*/


    }

    private fun createButtonBar() {
        btnHome = findViewById(R.id.btnHome)
        btnAlarms = findViewById(R.id.btnAlarms)
        btnContact = findViewById(R.id.btnContact)
        btnSettings = findViewById(R.id.btnSettings)

        createButtonBar(btnHome, btnAlarms, btnContact, btnSettings)
    }
}
