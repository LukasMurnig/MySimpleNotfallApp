package com.example.notfallapp.interfaces

import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import com.example.notfallapp.Login.SignUpActivity

interface ISOSOnClickListener {

    fun createSOSOnClickListener(context: Context, btnSos: Button){
        btnSos.setOnClickListener{
            // TODO Now open sign up, later send alert
            Log.d("SOSButton", "SOSButton was clicked!")
            val toast = Toast.makeText(context, "SORRY WE have not implemented yet!", Toast.LENGTH_SHORT)
            toast.show();
            /*val intent = Intent(context, SignUpActivity::class.java)
            startActivity(context, intent,null)*/
        }
    }
}