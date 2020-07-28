package com.example.notfallapp.menubar.contact

import android.content.Context
import android.content.Intent
import android.graphics.drawable.BitmapDrawable
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
import com.example.notfallapp.interfaces.ICheckPermission
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.IOException

class AddContactActivity: AppCompatActivity(), ICreatingOnClickListener, ICheckPermission {

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

    companion object{
        var phoneAreaCodes: MutableMap<String, String>? = null
        val timezones: MutableMap<String, String>? = null
        val countries: MutableMap<String, String>? = null
        val languages: MutableMap<String, String>? = null
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

                if(toUpdateContact == null){
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

                    installContact(contact, false)
                }else{
                    val gender: Int = if(spinnerGender.selectedItem == "Herr"){
                        1
                    }else{
                        0
                    }
                    toUpdateContact!!.forename = input_firstname.text.toString()
                    toUpdateContact!!.surname = input_lastname.text.toString()
                    toUpdateContact!!.gender = gender
                    toUpdateContact!!.e_mail = input_email.text.toString()
                    toUpdateContact!!.phoneFixed = input_number.text.toString()
                    toUpdateContact!!.messageType = spinnerMessage.selectedItem.toString()
                    if(toUpdateContact!!.pathToImage != null){
                        toUpdateContact!!.photoSet = true
                    }
                    toUpdateContact!!.pathToImage = path
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
        if(extras.getString(resources.getString(R.string.firstnameAlarmDatabase)) != null){
            toUpdateContact = Contact(null,
                extras.getString(resources.getString(R.string.firstnameAlarmDatabase)),
                extras.getString(resources.getString(R.string.lastnameAlarmDatabase)),
                extras.getBoolean(resources.getString(R.string.active)),
                "not implemented",
                extras.getInt("gender"),
                false,
                extras.getString(resources.getString(R.string.emailAlarmDatabase)),
                extras.getString(resources.getString(R.string.numberAlarmDatabas)),
                null,
                null,
                extras.getString("messageType"),
                extras.getInt(resources.getString(R.string.prio)),
                extras.getString(resources.getString(R.string.image)),
                null,
                null,
                null,
                null,
                null
            )

            if(toUpdateContact!!.pathToImage != null){
                toUpdateContact!!.photoSet = true
                path = toUpdateContact!!.pathToImage
                val bitmap =
                    MediaStore.Images.Media.getBitmap(contentResolver, Uri.parse(toUpdateContact!!.pathToImage))
                addpicture.background = BitmapDrawable(resources, bitmap)
            }

            input_firstname.setText(toUpdateContact!!.forename)
            input_lastname.setText(toUpdateContact!!.surname)
            input_email.setText(toUpdateContact!!.e_mail)
            input_number.setText(toUpdateContact!!.phoneFixed)
            spinnerGender.setSelection(toUpdateContact!!.gender)

            when (toUpdateContact!!.messageType){
                spinnerMessage.getItemAtPosition(0) -> spinnerMessage.setSelection(0)
                spinnerMessage.getItemAtPosition(1) -> spinnerMessage.setSelection(1)
                spinnerMessage.getItemAtPosition(2) -> spinnerMessage.setSelection(2)
            }
        }
    }

    private fun installContact(contact: Contact, update: Boolean){
        val appDb: EmergencyAppDatabase = EmergencyAppDatabase.getInstance(this)
        if(update){
            GlobalScope.launch {
                appDb.contactDao().updateContact(contact)
            }
        }else{
                GlobalScope.launch {
                    try{
                        appDb.contactDao().insertContact(contact)
                    }catch (ex: Exception){
                        // when unique constraint
                    }
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
        if(requestCode == 1 && data != null){
            path =  "content://media" + data.getStringExtra("path")
            if(path != null){
                try{
                    val uri = Uri.parse(path)
                    val bitmap =
                        MediaStore.Images.Media.getBitmap(applicationContext.contentResolver, uri)
                    addpicture.background = BitmapDrawable(resources, bitmap)
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

        spinnerGender.adapter = ArrayAdapter<String>(applicationContext, android.R.layout.simple_spinner_dropdown_item, arrayOf("Frau", "Herr"))
        spinnerMessage.adapter = ArrayAdapter<String>(applicationContext, android.R.layout.simple_spinner_dropdown_item, arrayOf("Anruf", "SMS", "Email"))

        // TODO get date vom Server Page 111 OrgUnitsItems
        // spinnerTelNr.adapter = ArrayAdapter<String>(applicationContext, android.R.layout.simple_spinner_item, arrayOf())
        // spinnerLanguage.adapter = ArrayAdapter<String>(applicationContext, android.R.layout.simple_spinner_item, arrayOf())
        // spinnerCountries.adapter = ArrayAdapter<String>(applicationContext, android.R.layout.simple_spinner_item, arrayOf())
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

        builder.setPositiveButton(resources.getString(R.string.Yes)) { dialog, _ ->
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            dialog.dismiss()
        }

        builder.setNegativeButton(resources.getString(R.string.No)) { dialog, _ ->
            dialog.dismiss()
        }
    }
}