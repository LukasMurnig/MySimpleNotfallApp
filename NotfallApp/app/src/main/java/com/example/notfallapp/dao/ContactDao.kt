package com.example.notfallapp.dao

import androidx.room.*
import com.example.notfallapp.bll.Contact

@Dao
interface ContactDao {
    @Query("SELECT * from Contact ORDER BY priority asc")
    fun getAllContact(): List<Contact>

    @Query("SELECT COUNT(*) FROM Contact")
    fun getCountOfContact(): Int

    @Query("SELECT * FROM Contact WHERE priority = :prio")
    fun getContactByPriority(prio: Int): Contact

    @Insert
    fun insertContact(contact: Contact)

    @Update
    fun updateContact(contact: Contact)

    @Delete
    fun deleteContact(contact: Contact)

    @Query("DELETE from Contact")
    fun deleteAll()
}