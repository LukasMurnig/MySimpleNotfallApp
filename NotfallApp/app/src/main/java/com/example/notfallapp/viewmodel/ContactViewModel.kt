package com.example.notfallapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.example.notfallapp.bll.Contact
import com.example.notfallapp.dao.ContactDao
import com.example.notfallapp.database.EmergencyAppDatabase

class ContactViewModel(application: Application) : AndroidViewModel(application) {
    private val contactDao: ContactDao = EmergencyAppDatabase.getDatabase(application).contactDao()
    val contactList: List<Contact>

    init {
        contactList = contactDao.getAllContact()
    }

    suspend fun insert(contacts: List<Contact>){
        contactDao.insertAllContacts(contacts)
    }

    suspend fun update(contact: Contact){
        contactDao.updateContact(contact)
    }

    suspend fun delete(contact: Contact){
        contactDao.deleteContact(contact)
    }
}