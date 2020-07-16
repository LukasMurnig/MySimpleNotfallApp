package com.example.notfallapp.bll

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Alarms")
data class Alarm constructor( val deviceId: String, val longitude: Double, val latitude: Double, val answeredContact: String, val alertTime: String) {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Int = 0
}