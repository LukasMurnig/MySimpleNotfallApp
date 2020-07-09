package com.example.notfallapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.example.notfallapp.bll.Contact
import com.example.notfallapp.dao.ContactDao
import com.example.notfallapp.database.DatabaseClient
import com.example.notfallapp.database.EmergencyAppDatabase

class ContactViewModel(application: Application) : AndroidViewModel(application) {
    var dbclient = DatabaseClient(application)
    var db = dbclient.getAppDatabase(application)
    private val contactDao: ContactDao? = db?.contactDao()
    lateinit var contactList: List<Contact>

    init {
        if (contactDao != null) {
            contactList = contactDao.getAllContact()
        }
    }

    suspend fun insert(contacts: List<Contact>){
        if (contactDao != null) {
            contactDao.insertAllContacts(contacts)
        }
    }

    suspend fun update(contact: Contact){
        if (contactDao != null) {
            contactDao.updateContact(contact)
        }
    }

    suspend fun delete(contact: Contact){
        if (contactDao != null) {
            contactDao.deleteContact(contact)
        }
    }
}