package com.example.notfallapp.menubar.contact

import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.notfallapp.R
import com.example.notfallapp.bll.AlertingChain
import com.example.notfallapp.interfaces.ICheckPermission
import com.example.notfallapp.interfaces.ICreatingOnClickListener
import com.example.notfallapp.server.ServerAlertingChain
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class ContactActivity: AppCompatActivity(), ICreatingOnClickListener, ICheckPermission {
    private lateinit var btnSos: Button
    private lateinit var btnHome: ImageButton
    private lateinit var btnContact: ImageButton
    private lateinit var btnAlarms: ImageButton
    private lateinit var btnSettings: ImageButton

    private lateinit var lbMessageNoContacts: TextView
    private lateinit var rvContacts: RecyclerView
    private lateinit var addButton: ImageButton

    companion object{
         var alertingChain: AlertingChain? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contacts)

        initComponents()

        /*addButton.setOnClickListener {
            Log.d(resources.getString(R.string.AddButton),
                  String.format(resources.getString(R.string.AddButtonContactMessage),
                    resources.getString(R.string.Contact)))
            val intent = Intent(this@ContactActivity, AddContactActivity::class.java)
            if(rvContacts.adapter != null){
                if(rvContacts.adapter!!.itemCount >= 3){
                    lbMessageNoContacts.text = resources.getString(R.string.allowedContacts)
                    return@setOnClickListener
                }
                intent.putExtra(resources.getString(R.string.prio), (rvContacts.adapter!!.itemCount))
            }else{
                intent.putExtra(resources.getString(R.string.prio), 0)
            }
            startActivity( intent, null)
        }*/

        MainScope().launch {
            ServerAlertingChain().getAlertingChain(applicationContext, rvContacts)

            /*try{
                getAllContacts()
            }catch (ex: Exception){
                Log.e(resources.getString(R.string.ExceptionDatabase),
                      String.format(resources.getString(R.string.ExceptionDatabaseMessage),
                                    resources.getString(R.string.Contact), ex.toString()))
            }*/
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
        lbMessageNoContacts = findViewById(R.id.lbMessageNoContacts)
        rvContacts = findViewById(R.id.rvContacts)
        //addButton = findViewById(R.id.addButton)

        rvContacts.setHasFixedSize(false)
        rvContacts.layoutManager = LinearLayoutManager(this)

        checkInternetGPSPermissions(this)
        createButtonBar()
    }
}