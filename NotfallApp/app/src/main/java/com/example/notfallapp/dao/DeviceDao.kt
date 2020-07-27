package com.example.notfallapp.dao

import androidx.room.*
import com.example.notfallapp.bll.Contact
import com.example.notfallapp.bll.Device

@Dao
interface DeviceDao {
    @Query("SELECT * FROM Devices")
    fun getDevice(): List<Device>

    @Insert
    fun insertDevice(device: Device)

    @Update
    fun updateDevice(device: Device)

    @Query("DELETE from Devices")
    fun deleteAll()
}