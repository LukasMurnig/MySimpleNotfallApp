package com.example.notfallapp.menubar

import android.widget.ImageButton
import com.example.notfallapp.R

interface ICreatingButtonBar {

    fun createButtonBar(btnHome: ImageButton, btnAlarms: ImageButton, btnContact: ImageButton, btnSettings: ImageButton){

        btnHome.setOnClickListener(){
            println("firstItem Click")
        }

        btnAlarms.setOnClickListener(){
            println("secondItem Click")
        }

        btnContact.setOnClickListener(){
            println("thirdItem Click")
        }

        btnSettings.setOnClickListener(){
            println("fourthItem Click")
        }
    }
}