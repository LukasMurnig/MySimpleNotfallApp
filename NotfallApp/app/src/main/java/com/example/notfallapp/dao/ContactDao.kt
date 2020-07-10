package com.example.notfallapp.dao

import androidx.room.*
import com.example.notfallapp.bll.Contact

@Dao
interface ContactDao {
    @Query("SELECT * from Contact ORDER BY lastname asc")
    fun getAllContact(): List<Contact>

    @Insert
    fun insertContact(contact: Contact)

    @Update
    fun updateContact(contact: Contact)

    @Delete
    fun deleteContact(contact: Contact)
}