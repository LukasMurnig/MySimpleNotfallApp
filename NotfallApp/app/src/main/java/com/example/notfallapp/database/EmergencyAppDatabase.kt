package com.example.notfallapp.database

import android.content.Context
import android.os.AsyncTask
import android.util.Log
import androidx.annotation.NonNull
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.notfallapp.bll.Contact
import com.example.notfallapp.dao.ContactDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope

@Database(entities = arrayOf(Contact::class), version = 1)
abstract class EmergencyAppDatabase : RoomDatabase(){
    abstract fun contactDao(): ContactDao

    /*companion object  {
        private var INSTANCE: EmergencyAppDatabase? = null
        private val DB_NAME: String = "emergency.db"

        fun getDatabase(context: Context): EmergencyAppDatabase {
            if (INSTANCE == null){
                synchronized(EmergencyAppDatabase::class.java) {
                    if (INSTANCE == null) {
                        System.out.println(context.applicationContext)
                        INSTANCE == Room.databaseBuilder(context.applicationContext, EmergencyAppDatabase::class.java, DB_NAME)
                            .build()
                    }
                }
            }
            return INSTANCE!!
        }
    }*/
}