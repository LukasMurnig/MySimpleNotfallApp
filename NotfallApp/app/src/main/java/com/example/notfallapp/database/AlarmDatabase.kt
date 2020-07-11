package com.example.notfallapp.database

import android.content.Context
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.notfallapp.bll.Alarm
import com.example.notfallapp.dao.AlarmsDao
import java.util.concurrent.locks.Lock

@Database(entities = [Alarm::class], version = 1, exportSchema = false)
abstract class AlarmDatabase : RoomDatabase() {

    abstract fun alarmsDao(): AlarmsDao

    companion object {
        @Volatile private var instance: AlarmDatabase? = null
        private val LOCK = Any()
        private val db_name: String = "alarms.db"

        operator fun invoke(context: Context)= instance ?: synchronized(LOCK) {
            instance ?: buildDatabase(context).also{ instance = it}
        }

        private fun buildDatabase(context: Context) = Room.databaseBuilder(context,
            AlarmDatabase::class.java, db_name)
            .build()

        // instance von Database wird anders geholt
        /*fun  getDatabase(context: Context): AlarmDatabase? {
            if(instance==null){
                synchronized(AlarmDatabase::class){
                    if(instance==null){
                        context.deleteDatabase(db_name)
                        instance = Room.databaseBuilder(context.applicationContext,
                            AlarmDatabase::class.java, db_name).allowMainThreadQueries()
                            .build()
                    }
                }
            }
            return instance
        }*/
    }
}
