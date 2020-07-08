package com.example.notfallapp

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.graphics.ColorFilter
import android.graphics.PorterDuff
import android.graphics.drawable.Icon
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.style.BackgroundColorSpan
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import androidx.appcompat.widget.Toolbar
import androidx.core.graphics.component1

class MainActivity : AppCompatActivity() {

    private lateinit var btnFirst : ImageButton
    private lateinit var btnDevice : ImageButton
    private lateinit var btnContacts : ImageButton
    private lateinit var btnSettings : ImageButton
    private lateinit var btnSos : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val toolbar = findViewById<Toolbar>(R.id.toolbar_main)
        toolbar.title=""
        setSupportActionBar(toolbar)

        createButtonBar()

        btnSos = findViewById(R.id.btn_sos)

        btnSos.setOnClickListener(){
            // TODO Now open sign up, later send alert
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu) : Boolean{
        menuInflater.inflate(R.menu.menubar, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item?.itemId){
            R.id.firstIcon -> {
                println("firstItem Click")
                return true
            }
            R.id.device -> {
                println("firstItem Click")
                return true
            }
            R.id.contacts -> {
                println("third Item click")
                return true
            }
            R.id.settings -> {
                println("fourth item click")
                return true
            }
        }
        return(super.onOptionsItemSelected(item));
    }

    private fun createButtonBar() {
        btnFirst = findViewById(R.id.btnFirst)
        btnDevice = findViewById(R.id.btnDevice)
        btnContacts = findViewById(R.id.btnContacts)
        btnSettings = findViewById(R.id.btnSettings)

        btnFirst.setOnClickListener(){
            println("firstItem Click")
        }

        btnDevice.setOnClickListener(){
            println("secondItem Click")
        }

        btnContacts.setOnClickListener(){
            println("thirdItem Click")
        }

        btnSettings.setOnClickListener(){
            println("fourthItem Click")
        }
    }
}
