package com.example.notfallapp.bll

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Contact")
class Contact constructor(var firstname: String, var lastname: String, var e_mail: String, var number: String, var priority: Int, var pathToImage: String, var active: Boolean) {
    @PrimaryKey
    @ColumnInfo(name = "email")
    var email: String = e_mail
}