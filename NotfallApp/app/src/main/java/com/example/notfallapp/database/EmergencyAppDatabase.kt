package com.example.notfallapp.database

import android.content.Context
import android.os.AsyncTask
import android.util.Log
import androidx.annotation.NonNull
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.notfallapp.bll.Alarm
import com.example.notfallapp.bll.Contact
import com.example.notfallapp.dao.AlarmsDao
import com.example.notfallapp.dao.ContactDao

@Database(entities = [Contact::class, Alarm::class], version = 2)
abstract class EmergencyAppDatabase : RoomDatabase(){
    abstract fun contactDao(): ContactDao
    abstract fun alarmsDao(): AlarmsDao

    /*companion object  {
        private var INSTANCE: EmergencyAppDatabase? = null
        private val DB_NAME: String = "emergency.db"

        operator fun invoke(context: Context)= INSTANCE ?: synchronized(LOCK) {
            INSTANCE ?: buildDatabase(context).also{ INSTANCE = it}
        }

        private fun buildDatabase(context: Context) = Room.databaseBuilder(context,
                EmergencyAppDatabase::class.java, "emergency.db")
                .build()

        /*private val DB_NAME: String = "emergency.db"

        fun getDatabase(context: Context): EmergencyAppDatabase? {
            if (INSTANCE == null){
                synchronized(EmergencyAppDatabase::class.java) {
                    if (INSTANCE == null) {
                        System.out.println(context.applicationContext)
                        INSTANCE == Room.databaseBuilder(context.applicationContext, EmergencyAppDatabase::class.java, DB_NAME)
                            .build()
                    }
                }
            }
            return INSTANCE
        }*/
    }
    //
    private class PopulateDbAsync(instance: com.example.notfallapp.database.EmergencyAppDatabase) :
        AsyncTask<Void?, Void?, Void?>() {
        private lateinit var alarmsDAO: AlarmsDao;
        protected override fun doInBackground(vararg p0: Void?): Void? {
            alarmsDAO.deleteAll()
            val g1 = Alarm("1", "Test", "zero")
            val g2 = Alarm("2", "Test", "zero")
            val g3 = Alarm("3", "Test", "zero")
            alarmsDAO.insertAlarm(g1, g2, g3)
            return null
        }

        init {
            alarmsDAO = INSTANCE?.alarmsDao()!!
        }
    }*/
}