package com.example.notfallapp.database

import android.content.Context
import androidx.room.Room


class DatabaseClient(context: Context?) {

    private var mInstance: DatabaseClient? = null
    private val DB_NAME: String = "emergency.db"
    //our app database object
    private var emergencyAppDatabase: EmergencyAppDatabase? = null



    @Synchronized
    fun getInstance(mCtx: Context): DatabaseClient? {
        if (mInstance == null) {
            mInstance = DatabaseClient(mCtx)
        }
        return mInstance
    }

    fun getAppDatabase(context: Context): EmergencyAppDatabase? {
        //creating the app database with Room database builder
        emergencyAppDatabase =
            Room.databaseBuilder<EmergencyAppDatabase>(context, EmergencyAppDatabase::class.java, DB_NAME).build()
        return emergencyAppDatabase
    }
}