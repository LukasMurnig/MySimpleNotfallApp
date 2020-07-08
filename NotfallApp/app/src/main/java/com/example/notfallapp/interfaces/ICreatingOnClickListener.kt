package com.example.notfallapp.interfaces

import android.widget.ImageButton
import com.example.notfallapp.R

interface ICreatingOnClickListener {

    fun createOnClickListener(btnHome: ImageButton, btnAlarms: ImageButton, btnContact: ImageButton, btnSettings: ImageButton){

        btnHome.setOnClickListener(){
            println("firstItem Click")
        }

        btnAlarms.setOnClickListener(){
            println("secondItem Click")
            // TODO Open register with alarms from Database
        }

        btnContact.setOnClickListener(){
            println("thirdItem Click")
        }

        btnSettings.setOnClickListener(){
            println("fourthItem Click")
        }
    }
}