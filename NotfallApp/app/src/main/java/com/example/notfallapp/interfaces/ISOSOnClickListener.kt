package com.example.notfallapp.interfaces

import android.content.Context
import android.content.Intent
import android.widget.Button
import androidx.core.content.ContextCompat.startActivity
import com.example.notfallapp.Login.SignUpActivity

interface ISOSOnClickListener {

    fun createSOSOnClickListener(context: Context, btnSos: Button){
        btnSos.setOnClickListener{
            // TODO Now open sign up, later send alert

            val intent = Intent(context, SignUpActivity::class.java)
            startActivity(context, intent,null)
        }
    }
}