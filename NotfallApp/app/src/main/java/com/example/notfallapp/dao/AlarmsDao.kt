package com.example.notfallapp.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.notfallapp.bll.Alarm

@Dao
interface AlarmsDao {
    @Query("SELECT * from Alarms")
    fun getAllAlarms(): List<Alarm>

    @Insert
    fun insertAlarm(alarm: Alarm)

    @Insert
    fun insertAlarm(vararg alarms: Alarm?)

    @Delete
    fun deleteAlarm(alarm: Alarm)

    @Query("DELETE from Alarms")
    fun deleteAll()
}