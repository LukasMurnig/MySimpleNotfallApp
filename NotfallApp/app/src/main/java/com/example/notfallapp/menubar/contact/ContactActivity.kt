package com.example.notfallapp.menubar.contact

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.notfallapp.MainActivity
import com.example.notfallapp.R
import com.example.notfallapp.adapter.ContactListAdapter
import com.example.notfallapp.bll.Contact
import com.example.notfallapp.database.DatabaseClient
import com.example.notfallapp.database.EmergencyAppDatabase
import com.example.notfallapp.interfaces.ICreatingOnClickListener
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class ContactActivity: AppCompatActivity(), ICreatingOnClickListener {

    private lateinit var btnSos: Button
    private lateinit var btnHome: ImageButton
    private lateinit var btnContact: ImageButton
    private lateinit var btnAlarms: ImageButton
    private lateinit var btnMap: ImageButton
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
            var intent = Intent(this, AddContactActivity::class.java)
            startActivity( intent, null)
        }

        try{
            getAllContacts()
        }catch (ex: Exception){
            Log.e("ExceptionDatabase", ex.toString())
        }
    }

    private fun createButtonBar() {
        // SOS Button
        btnSos = findViewById(R.id.btn_sos)

        // Button bar
        btnHome = findViewById(R.id.btnHome)
        btnAlarms = findViewById(R.id.btnAlarms)
        btnContact = findViewById(R.id.btnContact)
        btnMap = findViewById(R.id.btnMap)
        btnSettings = findViewById(R.id.btnSettings)

        createOnClickListener(this, btnSos, btnHome, btnAlarms, btnContact, btnMap, btnSettings)
    }

    private fun initComponents(){
        lbMessageContacts = findViewById(R.id.lbMessageContacts)
        lbMessageNoContacts = findViewById(R.id.lbMessageNoContacts)
        lvContacts = findViewById(R.id.lvContacts)
        addButton = findViewById(R.id.addButton)
    }

    private fun getAllContacts(){
        val appDb: EmergencyAppDatabase = EmergencyAppDatabase.getInstance(this)
        GlobalScope.launch {
            System.out.println("HELLO 1")
            val data  = appDb.contactDao().getAllContact()
            if (data != null) {
                if(data.isEmpty()) {
                    lbMessageNoContacts.setText(getResources().getString(R.string.noContacts))
                }else{
                    System.out.println("HELLO 2")
                    setAdapter(data)
                }
            }

        }
    }

    private fun setAdapter(data: List<Contact>){
        System.out.println("HELLO 3")
        val adapter = ContactListAdapter(this, data as ArrayList<Contact>)
        lvContacts.adapter = adapter;
    }
}