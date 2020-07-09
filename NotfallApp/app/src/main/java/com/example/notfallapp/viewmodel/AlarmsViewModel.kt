package com.example.notfallapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.notfallapp.bll.Alarm
import com.example.notfallapp.bll.Contact
import com.example.notfallapp.dao.AlarmsDao
import com.example.notfallapp.dao.ContactDao
import com.example.notfallapp.database.EmergencyAppDatabase

class AlarmsViewModel(application: Application) : AndroidViewModel(application) {
    private val alarmsDao: AlarmsDao = EmergencyAppDatabase.getDatabase(application).alarmsDao()
    val alarmsList: List<Alarm>

    init {
        alarmsList = alarmsDao.getAllAlarms()
    }

    suspend fun insert(alarm: Alarm){
        alarmsDao.insertAlarm(alarm)
    }

    suspend fun delete(alarm: Alarm){
        alarmsDao.deleteAlarm(alarm)
    }
}