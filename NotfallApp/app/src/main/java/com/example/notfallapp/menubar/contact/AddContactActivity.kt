package com.example.notfallapp.menubar.contact

import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.notfallapp.MainActivity
import com.example.notfallapp.R
import com.example.notfallapp.bll.Contact
import com.example.notfallapp.database.DatabaseClient
import com.example.notfallapp.database.EmergencyAppDatabase
import com.example.notfallapp.interfaces.ICreatingOnClickListener

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
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_addcontact)
        createButtonBar()

        initComponents()
        addpicture.setOnClickListener() {
            Log.d("AddButton", "Add Button to add picture to Contact were clicked!")
            var toast = Toast.makeText(this, "Sorry we have not implemented yet!", Toast.LENGTH_SHORT)
            toast.show()
        }

        btn_add.setOnClickListener() {
            Log.d("AddButton", "Add Button to add Contact were clicked!")

            // TODO check valid input!
            if(validate()){
                var contact = Contact(input_firstname.text.toString(), input_lastname.text.toString(),
                    input_email.text.toString(), input_number.text.toString().toInt(), 0)
                val appDb: EmergencyAppDatabase = EmergencyAppDatabase.getInstance(this)
                appDb.contactDao().insertContact(contact)
               var intent = Intent(this, ContactActivity::class.java)
                startActivity(intent)
            }
        }

        btn_cancel.setOnClickListener() {
            Log.d("CancelButton", "Cancel Button to add Contact were clicked!")
            sureDialog()
            val alert = builder.create()
            alert.show()
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
        var validate: Boolean = true

        var firstname: String? = input_firstname.getText().toString()
        var lastname: String? = input_lastname.getText().toString()
        var email: String? = input_email.getText().toString()
        try {
            input_number.text.toString().toInt()
        }catch (ex: Exception){
            Log.e("ParseException", ex.toString())
            input_number.setError("Telefonnummer darf nur Zahlen beinhalten!")
            validate = false
        }

        if (firstname?.isEmpty()!!) {
            input_firstname.setError("Vorname darf nicht leer sein")
            validate= false
        }else{
            input_firstname.setError(null)
        }

        if (lastname?.isEmpty()!!){
            input_lastname.setError("Nachname darf nicht leer sein!")
            validate = false
        }else {
            input_lastname.setError(null)
        }

        if (email?.isEmpty()!! || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            input_email.setError("Email darf nicht leer sein bzw. Email stimmt nicht!")
            validate = false
        }else{
            input_email.setError(null)
        }

        return validate
    }

    private fun sureDialog() {
        builder.setTitle(getResources().getString(R.string.confirm))
        builder.setMessage(getResources().getString(R.string.sureStopCreatingContact))

        builder.setPositiveButton(getResources().getString(R.string.Yes)) { dialog, which ->
            var intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            dialog.dismiss()
        }

        builder.setNegativeButton(getResources().getString(R.string.No)) {dialog, which ->
            dialog.dismiss()
        }
    }
}