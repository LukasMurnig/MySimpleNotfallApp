package com.example.notfallapp.menubar

import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.notfallapp.R
import com.example.notfallapp.adapter.ContactListAdapter
import com.example.notfallapp.bll.Contact
import com.example.notfallapp.database.DatabaseClient
import com.example.notfallapp.interfaces.ICreatingOnClickListener
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class ContactActivity: AppCompatActivity(), ICreatingOnClickListener {

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

        try{
            getAllContacts()
        }catch (ex: Exception){
            Log.e("ExceptionDatabase", ex.toString())
        }
    }

    private fun createButtonBar() {
        btnSos = findViewById(R.id.btn_sos)

        btnHome = findViewById(R.id.btnHome)
        btnAlarms = findViewById(R.id.btnAlarms)
        btnContact = findViewById(R.id.btnContact)
        btnSettings = findViewById(R.id.btnSettings)

        createOnClickListener(this, btnSos, btnHome, btnAlarms, btnContact, btnSettings)
    }

    private fun initComponents(){
        lbMessageContacts = findViewById(R.id.lbMessageContacts)
        lbMessageNoContacts = findViewById(R.id.lbMessageNoContacts)
        lvContacts = findViewById(R.id.lvContacts)
        addButton = findViewById(R.id.addButton)
    }

    private fun getAllContacts(){
        val dbclient = DatabaseClient(this)
        val db = dbclient.getAppDatabase(this)
        GlobalScope.launch {
            val data = db?.contactDao()?.getAllContact()
            if (data != null) {
                if(data.isEmpty()) {
                    lbMessageNoContacts.setText(getResources().getString(R.string.noContacts))
                }else{
                    setAdapter(data)
                }
            }

        }
    }

    private fun setAdapter(data: List<Contact>){
        val adapter = ContactListAdapter(this, data as ArrayList<Contact>)
        lvContacts.adapter = adapter;
    }
}