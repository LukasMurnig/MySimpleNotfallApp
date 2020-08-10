package com.example.notfallapp.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.notfallapp.bll.Device
import com.example.notfallapp.dao.DeviceDao

@Database(entities = [Device::class], version = 11)
abstract class EmergencyAppDatabase : RoomDatabase(){
    abstract fun deviceDao(): DeviceDao

    companion object {
        private var INSTANCE: EmergencyAppDatabase? = null
        private const val DB_NAME: String = "emergency.db"

        fun getInstance(context: Context): EmergencyAppDatabase {
            if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                        context.applicationContext,
                        EmergencyAppDatabase::class.java, DB_NAME
                        ).fallbackToDestructiveMigration()
                         .build()
            }
            return INSTANCE as EmergencyAppDatabase
        }
    }
}