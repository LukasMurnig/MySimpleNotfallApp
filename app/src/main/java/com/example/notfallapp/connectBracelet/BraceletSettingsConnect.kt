package com.example.notfallapp.connectBracelet

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import com.example.notfallapp.MainActivity
import com.example.notfallapp.R
import com.example.notfallapp.interfaces.ICheckPermission
import com.example.notfallapp.interfaces.ICreatingOnClickListener

/**
 * Class for the case that the user never connected to a bracelet
 */
class BraceletSettingsConnect: Activity(), ICreatingOnClickListener, ICheckPermission{

    private lateinit var backBtn: Button
    private lateinit var btnConnect: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bracelet_settings_connect)

        initComponents()

        initializeListener()
    }

    /**
     * Function for the initialization of the necessary components
     */
    private fun initComponents(){
        backBtn = findViewById(R.id.backBtn)
        btnConnect = findViewById(R.id.btnConnect)
    }

    /**
     * Function for the initialization of the necessary listeners
     */
    private fun initializeListener(){
        backBtn.setOnClickListener {
            val intent = Intent(applicationContext, MainActivity::class.java)
            startActivity(intent)
        }

        btnConnect.setOnClickListener {
            val intent = Intent(applicationContext, AddBraceletActivity::class.java)
            startActivity(intent)
        }
    }
}