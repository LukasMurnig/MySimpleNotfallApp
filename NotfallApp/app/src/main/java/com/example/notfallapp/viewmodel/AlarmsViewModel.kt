package com.example.notfallapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.room.Room
import com.example.notfallapp.bll.Alarm
import com.example.notfallapp.dao.AlarmsDao
import com.example.notfallapp.dao.ContactDao
import com.example.notfallapp.database.DatabaseClient

class AlarmsViewModel(application: Application) : AndroidViewModel(application) {
    var dbclient = DatabaseClient(application)
    var db = dbclient.getAppDatabase(application)
    private val alarmsDao: AlarmsDao? = db?.alarmsDao()
    lateinit var alarmsList: List<Alarm>

    init {
        if (alarmsDao != null) {
            alarmsList = alarmsDao.getAllAlarms()
        }
    }

    suspend fun insert(alarm: Alarm){
        if (alarmsDao != null) {
            alarmsDao.insertAlarm(alarm)
        }
    }

    suspend fun delete(alarm: Alarm){
        if (alarmsDao != null) {
            alarmsDao.deleteAlarm(alarm)
        }
    }
}