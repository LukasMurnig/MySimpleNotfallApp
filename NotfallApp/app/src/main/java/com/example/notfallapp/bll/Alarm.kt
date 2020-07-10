package com.example.notfallapp.bll

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Alarms")
class Alarm constructor( deviceId: String, deviceName: String, alertTime: String) {
    @PrimaryKey()
    @NonNull var deviceId: String = ""
    @ColumnInfo(name = "deviceName")
    var deviceName: String? = ""
    @ColumnInfo(name = "alertTime")
    var alertTime: String? = ""
}