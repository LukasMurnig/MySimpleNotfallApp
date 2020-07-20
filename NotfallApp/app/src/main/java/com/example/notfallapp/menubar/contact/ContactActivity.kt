package com.example.notfallapp.menubar.contact

import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.notfallapp.R
import com.example.notfallapp.adapter.ContactListAdapter
import com.example.notfallapp.bll.Contact
import com.example.notfallapp.database.EmergencyAppDatabase
import com.example.notfallapp.interfaces.ICreatingOnClickListener

class ContactActivity: AppCompatActivity(), ICreatingOnClickListener {

    private lateinit var btnSos: Button
    private lateinit var btnHome: ImageButton
    private lateinit var btnContact: ImageButton
    private lateinit var btnAlarms: ImageButton
    private lateinit var btnSettings: ImageButton

    private lateinit var lbMessageContacts: TextView
    private lateinit var lbMessageNoContacts: TextView
    private lateinit var rvContacts: RecyclerView
    private lateinit var addButton: ImageButton
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contacts)

        createButtonBar()

        initComponents()

        addButton.setOnClickListener {
            Log.d("AddButton", "Add Button to add contacts were clicked!")
            val intent = Intent(this, AddContactActivity::class.java)
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
        btnContact.setImageResource(R.drawable.contacts_active)
        btnSettings = findViewById(R.id.btnSettings)

        createOnClickListener(this, btnSos, btnHome, btnAlarms, btnContact, btnSettings)
    }

    private fun initComponents(){
        lbMessageContacts = findViewById(R.id.lbMessageContacts)
        lbMessageNoContacts = findViewById(R.id.lbMessageNoContacts)
        rvContacts = findViewById(R.id.rvContacts)
        addButton = findViewById(R.id.addButton)

        rvContacts.setHasFixedSize(false)
        rvContacts.layoutManager = LinearLayoutManager(this)
    }

    private fun getAllContacts(){
        /*val appDb: EmergencyAppDatabase = EmergencyAppDatabase.getInstance(this)
        GlobalScope.launch {
            println("HELLO 1")
            val data  = appDb.contactDao().getAllContact()
            if(data.isEmpty()) {
                lbMessageNoContacts.text = resources.getString(R.string.noContacts)
            }else{
                println("HELLO 2")
                setAdapter(data)
            }
        }*/
        class GetData : AsyncTask<Unit, Unit, List<Contact>>() {

            override fun doInBackground(vararg p0: Unit?): List<Contact> {
                val appDb: EmergencyAppDatabase = EmergencyAppDatabase.getInstance(this@ContactActivity)
                return appDb.contactDao().getAllContact()
            }

            override fun onPostExecute(result: List<Contact>?) {
                if(result != null){
                    if(result.isEmpty()){
                        lbMessageNoContacts.text = resources.getString(R.string.noContacts)
                    }else{
                        val adapter = ContactListAdapter(result)
                        rvContacts.adapter = adapter
                        adapter.notifyDataSetChanged()
                    }
                }
            }
        }

        val gd = GetData()
        gd.execute()
    }
}