package com.example.notfallapp.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.notfallapp.bll.Alarm
import com.example.notfallapp.dao.AlarmsDao


@Database(entities = [Alarm::class], version = 4, exportSchema = false)
abstract class AlarmDatabase : RoomDatabase() {

    abstract fun alarmsDao(): AlarmsDao

    companion object {
        @Volatile private var instance: AlarmDatabase? = null
        private val LOCK = Any()
        private val db_name: String = "alarms.db"

        operator fun invoke(context: Context)= instance ?: synchronized(LOCK) {
            instance ?: buildDatabase(context).also{ instance = it}
        }

        private val MIGRATION_1_2: Migration = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(
                    "ALTER TABLE users "
                            + "ADD COLUMN address TEXT"
                )
            }
        }

        private fun buildDatabase(context: Context) = Room.databaseBuilder(context,
            AlarmDatabase::class.java, db_name).addMigrations(MIGRATION_1_2)
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
