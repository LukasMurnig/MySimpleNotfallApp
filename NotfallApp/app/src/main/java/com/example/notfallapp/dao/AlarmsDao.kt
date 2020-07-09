package com.example.notfallapp.dao

import androidx.room.*
import com.example.notfallapp.bll.Alarm
import com.example.notfallapp.bll.Contact

@Dao
interface AlarmsDao {
    @Query("SELECT * from Alarms ORDER BY alertTime asc")
    fun getAllAlarms(): List<Alarm>

    @Insert
    fun insertAlarm(alarm: Alarm)

    @Delete
    fun deleteAlarm(alarm: Alarm)
}