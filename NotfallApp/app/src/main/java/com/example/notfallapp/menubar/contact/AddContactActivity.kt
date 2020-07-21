package com.example.notfallapp.menubar.contact

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.notfallapp.MainActivity
import com.example.notfallapp.R
import com.example.notfallapp.bll.Contact
import com.example.notfallapp.database.EmergencyAppDatabase
import com.example.notfallapp.interfaces.ICreatingOnClickListener
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.IOException

class AddContactActivity: AppCompatActivity(), ICreatingOnClickListener {

    private lateinit var btnSos: Button
    private lateinit var btnHome: ImageButton
    private lateinit var btnContact: ImageButton
    private lateinit var btnAlarms: ImageButton
    private lateinit var btnSettings: ImageButton

    private lateinit var addpicture: ImageButton
    private lateinit var btn_add: Button
    private lateinit var btn_cancel: Button
    private lateinit var input_firstname: EditText
    private lateinit var input_lastname: EditText
    private lateinit var input_email: EditText
    private lateinit var input_number: EditText
    private lateinit var builder: AlertDialog.Builder
    private var path: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_addcontact)
        createButtonBar()

        initComponents()
        addpicture.setOnClickListener {
            Log.d("AddButton", "Add Button to add picture to Contact were clicked!")
            val intent = Intent(this, SelectContactPictureActivity::class.java)
            startActivityForResult(intent, 1)
        }

        btn_add.setOnClickListener {
            Log.d("AddButton", "Add Button to add Contact were clicked!")

            if(validate()){
                if(path == null){
                    path = ""
                }
                val contact = Contact(input_firstname.text.toString(), input_lastname.text.toString(),
                    input_email.text.toString(), input_number.text.toString(), path!!
                )
                val appDb: EmergencyAppDatabase = EmergencyAppDatabase.getInstance(this)
                GlobalScope.launch {
                    appDb.contactDao().deleteAll()
                    appDb.contactDao().insertContact(contact)
                }

               val intent = Intent(this, ContactActivity::class.java)
                startActivity(intent)
            }
        }

        btn_cancel.setOnClickListener {
            Log.d("CancelButton", "Cancel Button to add Contact were clicked!")
            sureDialog()
            val alert = builder.create()
            alert.show()
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
                    Log.e("image", e.printStackTrace().toString())
                }
            }
        }
    }

    private fun initComponents() {
        addpicture = findViewById(R.id.addpicture)
        btn_add = findViewById(R.id.btn_add)
        btn_cancel = findViewById(R.id.btn_cancel)
        input_firstname = findViewById(R.id.input_firstname)
        input_lastname = findViewById(R.id.input_lastname)
        input_email = findViewById(R.id.input_email)
        input_number = findViewById(R.id.input_number)
        builder = AlertDialog.Builder(this)
    }

    private fun validate(): Boolean {
        var validate = true

        val firstname: String? = input_firstname.text.toString()
        val lastname: String? = input_lastname.text.toString()
        val email: String? = input_email.text.toString()
        val telNr: String? = input_number.text.toString()

        if (firstname?.isEmpty()!!) {
            input_firstname.error = "Vorname darf nicht leer sein"
            validate= false
        }else{
            input_firstname.error = null
        }

        if (lastname?.isEmpty()!!){
            input_lastname.error = "Nachname darf nicht leer sein!"
            validate = false
        }else {
            input_lastname.error = null
        }

        if (email?.isEmpty()!! || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            input_email.error = "Email darf nicht leer sein bzw. Email stimmt nicht!"
            validate = false
        }else{
            input_email.error = null
        }

        if(telNr?.isEmpty()!! || !android.util.Patterns.PHONE.matcher(telNr).matches()){
            input_number.error = "Telefonnummer darf nur Zahlen beinhalten!"
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