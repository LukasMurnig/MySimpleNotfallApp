package com.example.notfallapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import android.widget.ListView
import android.widget.TextView
import com.example.notfallapp.Login.SignUpActivity
import com.example.notfallapp.interfaces.ICreatingOnClickListener
import com.example.notfallapp.interfaces.ISOSOnClickListener
import com.example.notfallapp.menubar.ContactActivity

class MainActivity : AppCompatActivity(),
    ICreatingOnClickListener, ISOSOnClickListener {

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
        createSOSOnClickListener(this, btnSos)
    }

    private fun createButtonBar() {
        btnHome = findViewById(R.id.btnHome)
        btnAlarms = findViewById(R.id.btnAlarms)
        btnContact = findViewById(R.id.btnContact)
        btnSettings = findViewById(R.id.btnSettings)

        createOnClickListener(this, btnHome, btnAlarms, btnContact, btnSettings)
    }
}
