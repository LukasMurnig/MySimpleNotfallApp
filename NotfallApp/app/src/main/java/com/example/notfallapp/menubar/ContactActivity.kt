package com.example.notfallapp.menubar

import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.notfallapp.R
import com.example.notfallapp.interfaces.ICreatingOnClickListener
import com.example.notfallapp.interfaces.ISOSOnClickListener

class ContactActivity: AppCompatActivity(), ICreatingOnClickListener, ISOSOnClickListener {

    private lateinit var btnSos: Button
    private lateinit var btnHome: ImageButton
    private lateinit var btnContact: ImageButton
    private lateinit var btnAlarms: ImageButton
    private lateinit var btnSettings: ImageButton
    private lateinit var lbMessageContacts: TextView
    private lateinit var lbMessageNoContacts: TextView
    private lateinit var lvContacts: ListView
    private lateinit var addButton: ImageButton
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contacts)

        createButtonBar()

        initComponents()

        addButton.setOnClickListener() {
            Log.d("AddButton", "Add Button to add contacts were clicked!")
            var toast = Toast.makeText(this, "Sorry we have not implemented yet!", Toast.LENGTH_SHORT)
            toast.show()
        }
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

    private fun initComponents(){
        lbMessageContacts = findViewById(R.id.lbMessageContacts)
        lbMessageNoContacts = findViewById(R.id.lbMessageNoContacts)
        lvContacts = findViewById(R.id.lvContacts)
        addButton = findViewById(R.id.addButton)
    }
}