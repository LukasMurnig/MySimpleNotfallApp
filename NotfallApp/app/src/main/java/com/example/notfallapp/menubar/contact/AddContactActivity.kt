package com.example.notfallapp.menubar.contact

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.notfallapp.R
import com.example.notfallapp.bll.Contact
import com.example.notfallapp.database.EmergencyAppDatabase
import com.example.notfallapp.interfaces.ICheckPermission
import com.example.notfallapp.interfaces.IContact
import com.example.notfallapp.interfaces.ICreatingOnClickListener
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class AddContactActivity: AppCompatActivity(), ICreatingOnClickListener, ICheckPermission, IContact {

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
    private lateinit var spinnerCountries: Spinner

    private lateinit var builder: AlertDialog.Builder
    private var path: String? = null
    private var prio: Int? = null

    companion object{
        var phoneAreaCodes = arrayOf<String>()
        var timezones = arrayOf<String>()
        var countries = arrayOf<String>()
        var languages = arrayOf<String>()
    }

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
                var photoSet = false
                if(path != null){
                    photoSet = true
                }

                val gender: Int = if(spinnerGender.selectedItem == "Herr"){
                    1
                }else{
                    0
                }
                val contact = Contact(null, input_firstname.text.toString(), input_lastname.text.toString(), true,
                    "not implemented", gender, photoSet, input_email.text.toString(), input_number.text.toString(),
                    null, null, spinnerMessage.selectedItem.toString(), prio!!, path,
                    "not implemented", "not implemented", 1111, "not implemented", "not implemented"
                )

                installContact(contact)
            }
        }

        initCancelButton(applicationContext, btn_cancel, builder)

        val extras = intent.extras ?: return
        prio = extras.getInt(resources.getString(R.string.prio))
    }

    private fun installContact(contact: Contact){
        GlobalScope.launch {
            try{
                EmergencyAppDatabase.getInstance(applicationContext).contactDao().insertContact(contact)
            }catch (ex: Exception){
                // when unique constraint
                Log.e("Database", "unique constraint, this email already exist")
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == 1 && data != null){
            path = setImage(data, applicationContext, addpicture)
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
        spinnerTimezone = findViewById(R.id.spinnerTimezone)
        spinnerMessage = findViewById(R.id.spinnerMessage)
        spinnerCountries = findViewById(R.id.spinnerCountry)

        builder = AlertDialog.Builder(this)

        spinnerGender.adapter = ArrayAdapter<String>(applicationContext, R.layout.spinner_layout, arrayOf("Frau", "Herr"))
        spinnerMessage.adapter = ArrayAdapter<String>(applicationContext, R.layout.spinner_layout, arrayOf("Anruf", "SMS", "Email"))

        // TODO get date vom Server Page 111 OrgUnitsItems
        // Zurzeit hardcodiert bsp. reintun
        spinnerTelNr.adapter = ArrayAdapter<String>(applicationContext, R.layout.spinner_layout, phoneAreaCodes)
        spinnerLanguage.adapter = ArrayAdapter<String>(applicationContext, R.layout.spinner_layout, languages)
        spinnerCountries.adapter = ArrayAdapter<String>(applicationContext, R.layout.spinner_layout, countries)
        spinnerTimezone.adapter = ArrayAdapter<String>(applicationContext, R.layout.spinner_layout, timezones)

        checkInternetGPSPermissions(this)
    }
}