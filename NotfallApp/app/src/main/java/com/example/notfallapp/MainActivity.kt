package com.example.notfallapp

import android.content.ClipData
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.ImageButton
import androidx.appcompat.widget.Toolbar
import com.example.notfallapp.Login.SignUpActivity
import com.example.notfallapp.menubar.ContactActivity

class MainActivity : AppCompatActivity() {

    private lateinit var btnSos: Button
    private lateinit var btnHome: ImageButton
    private lateinit var btnContact: ImageButton
    private lateinit var btnAlarms: ImageButton
    private lateinit var btnSettings: ImageButton
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        /*val toolbar = findViewById<Toolbar>(R.id.toolbar_main)
        setMainActivityControls()
        toolbar.title=""
        setSupportActionBar(toolbar)*/

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

        btnAlarms.setOnClickListener(){
            // TODO Open register with alarms from Database
            Log.d("MenuItemClicked", "Alarmregister were clicked in MainActivity")
        }

        btnContact.setOnClickListener(){
            // TODO open ContactActivity
            Log.d("MenuItemClicked", "Contacts were clicked in MainActivity")
            val intent = Intent(this, ContactActivity::class.java)
            startActivity(intent)
        }

        btnSettings.setOnClickListener(){
            // TODO open settingsActivity
            Log.d("MenuItemClicked", "Settings were clicked in MainActivity")
        }
    }

    /*override fun onCreateOptionsMenu(menu: Menu) : Boolean{
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
    }*/

    private fun createButtonBar() {
        btnHome = findViewById(R.id.btnFirst)
        btnAlarms = findViewById(R.id.btnDevice)
        btnContact = findViewById(R.id.btnContacts)
        btnSettings = findViewById(R.id.btnSettings)

        btnHome.setOnClickListener(){
            println("firstItem Click")
        }

        btnAlarms.setOnClickListener(){
            println("secondItem Click")
        }

        btnContact.setOnClickListener(){
            println("thirdItem Click")
        }

        btnSettings.setOnClickListener(){
            println("fourthItem Click")
        }
    }
}
