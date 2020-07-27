package com.example.notfallapp.bll

import androidx.room.Entity

@Entity(tableName = "device")
class Device constructor( val deviceAddress: String) {
}