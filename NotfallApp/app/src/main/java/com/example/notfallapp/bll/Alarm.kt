package com.example.notfallapp.bll

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.sql.Time

@Entity(tableName = "Alarms")
class Alarm constructor( deviceId: String, deviceName: String, alertTime: String) {
    @PrimaryKey
    var deviceId: String = ""
    @ColumnInfo(name = "deviceName")
    var deviceName: String? = ""
    @ColumnInfo(name = "alertTime")
    var alertTime: String? = null
}