package com.example.notfallapp.bll

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Contact")
class Contact constructor( firstname: String, lastname: String, email: String, number: Int) {
    @ColumnInfo(name = "firstname")
    var firstname: String? = ""
    @ColumnInfo(name = "lastname")
    var lastname: String? = ""
    @PrimaryKey
    var email: String = ""
    @ColumnInfo(name = "number")
    var number: Int? = 0
}