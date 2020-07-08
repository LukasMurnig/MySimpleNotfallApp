package com.example.notfallapp.databasemanager

import com.example.notfallapp.bll.Contact
import com.google.gson.Gson
import java.lang.reflect.Type
import java.util.*

class DatabaseManager {
    companion object{
        private var databaseManger : DatabaseManager? = null
        private var ipHost : String = "192.168.44.133:8080"
    }

    private fun DatabaseManager() : DatabaseManager? {
       return null
    }

    fun newInstance() : DatabaseManager? {
        if (DatabaseManager.databaseManger == null) {
            DatabaseManager.databaseManger = DatabaseManager()
        }
        return DatabaseManager.databaseManger
    }

    fun newInstance(ip: String?) : DatabaseManager? {
        if (DatabaseManager.databaseManger == null) {
            DatabaseManager.databaseManger = DatabaseManager()
        }
        ipHost = ip!!
        return DatabaseManager.databaseManger
    }
}