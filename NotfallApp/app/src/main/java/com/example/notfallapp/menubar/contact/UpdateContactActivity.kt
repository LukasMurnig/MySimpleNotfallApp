package com.example.notfallapp.menubar.contact

import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
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

class UpdateContactActivity: AppCompatActivity(), ICreatingOnClickListener, ICheckPermission, IContact {

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
    private lateinit var toUpdateContact: Contact

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_addcontact)

        initComponents()

        addpicture.setOnClickListener {
            Log.d(resources.getString(R.string.AddButton),
                String.format(resources.getString(R.string.AddButtonPictureMessage),
                    resources.getString(R.string.AddContact)))
            val intent = Intent(this, SelectContactPictureActivity::class.java)
            startActivityForResult(intent, 1)
        }

        initCancelButton(applicationContext, btn_cancel, builder)

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
                toUpdateContact.forename = input_firstname.text.toString()
                toUpdateContact.surname = input_lastname.text.toString()
                toUpdateContact.gender = gender
                toUpdateContact.e_mail = input_email.text.toString()
                toUpdateContact.phoneFixed = input_number.text.toString()
                toUpdateContact.messageType = spinnerMessage.selectedItem.toString()
                toUpdateContact.photoSet = photoSet
                if(toUpdateContact.pathToImage != null){
                    toUpdateContact.photoSet = true
                }
                toUpdateContact.pathToImage = path
                updateContact(toUpdateContact)
            }
        }

        /* falls nur alertingChainmember und nicht contact zu updaten sind
        val extras = intent.extras ?: return
        lateinit var toUpdateAlertingChainMember: AlertingChainMember
        ContactActivity.alertingChain.helpers!!.forEach { acm ->
            if(acm.helperId == extras.get("id") as UUID)
            {
                toUpdateAlertingChainMember = acm
            }
        }
        prio = toUpdateAlertingChainMember.rank

        input_firstname.setText(toUpdateAlertingChainMember.helperForename)
        input_lastname.setText(toUpdateAlertingChainMember.helperSurname)
        input_email.setText(toUpdateAlertingChainMember.email)
        input_number.setText(toUpdateAlertingChainMember.phoneNumber)
        //spinnerGender.setSelection(toUpdateAlertingChainMember.)*/

        val extras = intent.extras ?: return
        prio = extras.getInt(resources.getString(R.string.prio))
        if(extras.getString(resources.getString(R.string.firstnameAlarmDatabase)) != null){
            toUpdateContact = Contact(null,
                extras.getString(resources.getString(R.string.firstnameAlarmDatabase)) as String,
                extras.getString(resources.getString(R.string.lastnameAlarmDatabase)) as String,
                extras.getBoolean(resources.getString(R.string.active)),
                "not implemented",
                extras.getInt("gender"),
                false,
                extras.getString(resources.getString(R.string.emailAlarmDatabase)) as String,
                extras.getString(resources.getString(R.string.numberAlarmDatabas)) as String,
                null,
                null,
                extras.getString("messageType") as String,
                extras.getInt(resources.getString(R.string.prio)),
                extras.getString(resources.getString(R.string.image)),
                null,
                null,
                null,
                null,
                null
            )

            if(toUpdateContact.pathToImage != null){
                toUpdateContact.photoSet = true
                path = toUpdateContact.pathToImage
                val bitmap =
                    MediaStore.Images.Media.getBitmap(contentResolver, Uri.parse(toUpdateContact.pathToImage))
                addpicture.background = BitmapDrawable(resources, bitmap)
            }

            input_firstname.setText(toUpdateContact.forename)
            input_lastname.setText(toUpdateContact.surname)
            input_email.setText(toUpdateContact.e_mail)
            input_number.setText(toUpdateContact.phoneFixed)
            spinnerGender.setSelection(toUpdateContact.gender)

            when (toUpdateContact.messageType){
                spinnerMessage.getItemAtPosition(0) -> spinnerMessage.setSelection(0)
                spinnerMessage.getItemAtPosition(1) -> spinnerMessage.setSelection(1)
                spinnerMessage.getItemAtPosition(2) -> spinnerMessage.setSelection(2)
            }
        }
    }

    private fun updateContact(contact: Contact){
        val appDb: EmergencyAppDatabase = EmergencyAppDatabase.getInstance(this)
        GlobalScope.launch {
            appDb.contactDao().updateContact(contact)
        }
        val intent = Intent(this, ContactActivity::class.java)
        startActivity(intent)
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
        createButtonBar()

        addpicture = findViewById(R.id.addpicture)
        btn_add = findViewById(R.id.btn_add)
        btn_add.text = resources.getText(R.string.updateProfil)
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
        spinnerTelNr.adapter = ArrayAdapter<String>(applicationContext, R.layout.spinner_layout,
            AddContactActivity.phoneAreaCodes
        )
        spinnerLanguage.adapter = ArrayAdapter<String>(applicationContext, R.layout.spinner_layout,
            AddContactActivity.languages
        )
        spinnerCountries.adapter = ArrayAdapter<String>(applicationContext, R.layout.spinner_layout,
            AddContactActivity.countries
        )
        spinnerTimezone.adapter = ArrayAdapter<String>(applicationContext, R.layout.spinner_layout,
            AddContactActivity.timezones
        )

        checkInternetGPSPermissions(this)
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
}