package com.example.notfallapp

import android.content.ClipData
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import androidx.appcompat.widget.Toolbar
import com.example.notfallapp.Login.SignUpActivity
import com.example.notfallapp.menubar.ContactActivity

class MainActivity : AppCompatActivity() {

    private lateinit var btnSos: Button
    private lateinit var btnHome: MenuItem
    private lateinit var btnContact: MenuItem
    private lateinit var btnAlarms: MenuItem
    private lateinit var btnSettings: MenuItem
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setMainActivityControls()
        var toolbar = findViewById<Toolbar>(R.id.toolbar_main)
        toolbar.title=""
        setSupportActionBar(toolbar)



        btnSos.setOnClickListener(){
            // TODO Now open sign up, later send alert
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }

        /* btnAlarms.setOnMenuItemClickListener(){
            // TODO Open register with alarms from Database
        }*/

        btnAlarms.setOnMenuItemClickListener(){
            // TODO Open register with alarms from Database
            Log.d("MenuItemClicked", "Alarmregister were clicked in MainActivity")
            return@setOnMenuItemClickListener true
        }

        btnContact.setOnMenuItemClickListener(){
            // TODO open ContactActivity
            Log.d("MenuItemClicked", "Contacts were clicked in MainActivity")
            val intent = Intent(this, ContactActivity::class.java)
            startActivity(intent)
            return@setOnMenuItemClickListener true
        }

        btnSettings.setOnMenuItemClickListener(){
            // TODO open settingsActivity
            Log.d("MenuItemClicked", "Settings were clicked in MainActivity")
            return@setOnMenuItemClickListener true
        }
    }

    override fun onCreateOptionsMenu(menu: Menu) : Boolean{
        menuInflater.inflate(R.menu.menubar, menu)
        btnHome = menu.findItem(R.id.firstIcon)
        btnAlarms = menu.findItem(R.id.secondIcon)
        btnContact = menu.findItem(R.id.thirdIcon)
        btnSettings = menu.findItem(R.id.fourthIcon)
        return true
    }

    fun setMainActivityControls(){
        btnSos = findViewById(R.id.btn_sos)
    }
}
