package com.example.notfallapp.menubar.contact

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.Uri
import android.net.wifi.WifiManager
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.notfallapp.MainActivity
import com.example.notfallapp.R
import com.example.notfallapp.bll.Contact
import com.example.notfallapp.database.EmergencyAppDatabase
import com.example.notfallapp.interfaces.ICreatingOnClickListener
import com.example.notfallapp.interfaces.checkPermission
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.IOException

class AddContactActivity: AppCompatActivity(), ICreatingOnClickListener, checkPermission {

    private lateinit var btnSos: Button
    private lateinit var btnHome: ImageButton
    private lateinit var btnContact: ImageButton
    private lateinit var btnAlarms: ImageButton
    private lateinit var btnSettings: ImageButton

    private lateinit var addpicture: ImageButton
    private lateinit var btn_add: Button
    private lateinit var btn_cancel: Button
    private lateinit var spinnerGender: Spinner
    private lateinit var input_firstname: EditText
    private lateinit var input_lastname: EditText
    private lateinit var input_email: EditText
    private lateinit var spinnerTelNr: Spinner
    private lateinit var input_number: EditText
    private lateinit var spinnerLanguage: Spinner
    private lateinit var spinnerTimezone: Spinner
    private lateinit var spinnerMessage: Spinner

    private lateinit var builder: AlertDialog.Builder
    private var path: String? = null
    private var prio: Int? = null
    private var toUpdateContact: Contact? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_addcontact)
        createButtonBar()

        initComponents()
        addpicture.setOnClickListener {
            Log.d(resources.getString(R.string.AddButton),
                  String.format(resources.getString(R.string.AddButtonPictureMessage),
                                resources.getString(R.string.AddContact)))
            val intent = Intent(this, SelectContactPictureActivity::class.java)
            startActivityForResult(intent, 1)
        }

        btn_add.setOnClickListener {
            Log.d(resources.getString(R.string.AddButton),
                  String.format(resources.getString(R.string.AddButtonContactMessage),
                                resources.getString(R.string.AddContact)))

            if(validate()){
                if(path == null){
                    path = ""
                }
                if(toUpdateContact==null){
                    val contact = Contact(input_firstname.text.toString(), input_lastname.text.toString(),
                        input_email.text.toString(), input_number.text.toString(), prio!!,  path!!, true
                    )

                    installContact(contact, false)
                }else{
                    toUpdateContact!!.firstname = input_firstname.text.toString()
                    toUpdateContact!!.lastname = input_lastname.text.toString()
                    toUpdateContact!!.e_mail = input_email.text.toString()
                    toUpdateContact!!.number = input_number.text.toString()
                    toUpdateContact!!.pathToImage = path as String
                    installContact(toUpdateContact!!, true)
                }
            }
        }

        btn_cancel.setOnClickListener {
            Log.d(resources.getString(R.string.CancelButton),
                String.format(resources.getString(R.string.CancelButtonClicked),
                    resources.getString(R.string.AddContact)))
            sureDialog()
            val alert = builder.create()
            alert.show()
        }

        val extras = intent.extras ?: return
        prio = extras.getInt(resources.getString(R.string.prio))
        if(extras.getString(resources.getString(R.string.firstnameAlarmDatabase))!=null){
            toUpdateContact = Contact(extras.getString(resources.getString(R.string.firstnameAlarmDatabase)),
                                      extras.getString(resources.getString(R.string.lastnameAlarmDatabase)),
                                      extras.getString(resources.getString(R.string.emailAlarmDatabase)),
                                      extras.getString(resources.getString(R.string.numberAlarmDatabas)),
                                      extras.getInt(resources.getString(R.string.prio)),
                                      extras.getString(resources.getString(R.string.image)),
                                      extras.getBoolean(resources.getString(R.string.active))
            )

            if(toUpdateContact!!.pathToImage.isNotEmpty()){
                val bitmap =
                    MediaStore.Images.Media.getBitmap(contentResolver, Uri.parse(toUpdateContact!!.pathToImage))
                addpicture.setImageBitmap(bitmap)
            }

            input_firstname.setText(toUpdateContact!!.firstname)
            input_lastname.setText(toUpdateContact!!.lastname)
            input_email.setText(toUpdateContact!!.e_mail)
            input_number.setText(toUpdateContact!!.number)
        }
    }

    private fun installContact(contact: Contact, update: Boolean){
        val appDb: EmergencyAppDatabase = EmergencyAppDatabase.getInstance(this)
        if(update){
            GlobalScope.launch {
                appDb.contactDao().updateContact(contact!!)
            }
        }else{
            GlobalScope.launch {
                appDb.contactDao().insertContact(contact)
            }
        }
        val intent = Intent(this, ContactActivity::class.java)
        startActivity(intent)
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_CANCELED) {
            return
        }
        if(requestCode== 1 && data != null){
            path =  "content://media" + data.getStringExtra("path")
            if(path != null){
                try{
                    val uri = Uri.parse(path)
                    val bitmap =
                        MediaStore.Images.Media.getBitmap(applicationContext.contentResolver, uri)
                    addpicture.setImageBitmap(bitmap)
                }catch (e: IOException){
                    Log.e(resources.getString(R.string.image),
                          String.format(resources.getString(R.string.Image),
                                        resources.getString(R.string.AddContact), e.toString()))
                }
            }
        }
    }

    private fun initComponents() {
        addpicture = findViewById(R.id.addpicture)
        btn_add = findViewById(R.id.btn_add)
        btn_cancel = findViewById(R.id.btn_cancel)
        spinnerGender = findViewById(R.id.spinnerGender)
        input_firstname = findViewById(R.id.input_firstname)
        input_lastname = findViewById(R.id.input_lastname)
        input_email = findViewById(R.id.input_email)
        spinnerTelNr = findViewById(R.id.spinnerTelNr)
        input_number = findViewById(R.id.input_number)
        spinnerLanguage = findViewById(R.id.spinnerLanguage)
        spinnerGender = findViewById(R.id.spinnerGender)
        spinnerMessage = findViewById(R.id.spinnerMessage)

        builder = AlertDialog.Builder(this)

        spinnerGender.adapter = ArrayAdapter<String>(applicationContext, android.R.layout.simple_spinner_dropdown_item, arrayOf("Herr", "Frau"))
        spinnerMessage.adapter = ArrayAdapter<String>(applicationContext, android.R.layout.simple_spinner_dropdown_item, arrayOf("Anruf", "SMS", "Email"))

        // TODO get date vom Server Page 111 OrgUnitsItems
        // spinnerTelNr.adapter = ArrayAdapter<String>(applicationContext, android.R.layout.simple_spinner_item, arrayOf())
        // spinnerLanguage.adapter = ArrayAdapter<String>(applicationContext, android.R.layout.simple_spinner_item, arrayOf())
        // spinnerGender.adapter = ArrayAdapter<String>(applicationContext, android.R.layout.simple_spinner_item, arrayOf())
        val connectivityManager =
            getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val wifi =
            getSystemService(Context.WIFI_SERVICE) as WifiManager
        checkInternetAccess(this, connectivityManager, wifi)
    }

    private fun validate(): Boolean {
        var validate = true

        val firstname: String? = input_firstname.text.toString()
        val lastname: String? = input_lastname.text.toString()
        val email: String? = input_email.text.toString()
        val telNr: String? = input_number.text.toString()

        if (firstname?.isEmpty()!!) {
            input_firstname.error = resources.getString(R.string.firstnameEmpty)
            validate= false
        }else{
            input_firstname.error = null
        }

        if (lastname?.isEmpty()!!){
            input_lastname.error = resources.getString(R.string.lastnameEmpty)
            validate = false
        }else {
            input_lastname.error = null
        }

        if (email?.isEmpty()!! || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            input_email.error = resources.getString(R.string.emailEmpty)
            validate = false
        }else{
            input_email.error = null
        }

        if(telNr?.isEmpty()!! || !android.util.Patterns.PHONE.matcher(telNr).matches()){
            input_number.error = resources.getString(R.string.numberWrongType)
            validate = false
        }else{
            input_number.error = null
        }

        return validate
    }

    private fun sureDialog() {
        builder.setTitle(resources.getString(R.string.confirm))
        builder.setMessage(resources.getString(R.string.sureStopCreatingContact))

        builder.setPositiveButton(getResources().getString(R.string.Yes)) { dialog, which ->
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            dialog.dismiss()
        }

        builder.setNegativeButton(getResources().getString(R.string.No)) {dialog, which ->
            dialog.dismiss()
        }
    }
}