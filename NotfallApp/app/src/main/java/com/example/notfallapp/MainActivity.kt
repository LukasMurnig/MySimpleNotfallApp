package com.example.notfallapp

import android.content.ClipData
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.ImageButton
import androidx.appcompat.widget.Toolbar
import com.example.notfallapp.Login.SignUpActivity

class MainActivity : AppCompatActivity() {

    private lateinit var btnSos: Button
    private lateinit var btnHome: MenuItem
    private lateinit var btnContact: MenuItem
    private lateinit var btnAlarms: MenuItem
    private lateinit var btnSettings: MenuItem
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
        btnHome = menu.findItem(R.id.btnFirst)
        btnAlarms = menu.findItem(R.id.btnDevice)
        btnContact = menu.findItem(R.id.btnContacts)
        btnSettings = menu.findItem(R.id.btnSettings)

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
