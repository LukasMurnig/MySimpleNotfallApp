package com.example.notfallapp.bll

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Contact")
class Contact constructor( val firstname: String, val lastname: String, val e_mail: String, val number: String, val imageResourceId: Int) {
    @PrimaryKey
    @ColumnInfo(name = "email")
    var email: String = e_mail
}