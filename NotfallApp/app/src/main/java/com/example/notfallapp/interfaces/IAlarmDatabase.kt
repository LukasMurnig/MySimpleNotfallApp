package com.example.notfallapp.interfaces

import android.content.Intent
import android.os.AsyncTask
import android.view.View
import com.example.notfallapp.R
import com.example.notfallapp.adapter.ContactListAdapter
import com.example.notfallapp.bll.Contact
import com.example.notfallapp.database.EmergencyAppDatabase
import com.example.notfallapp.menubar.contact.AddContactActivity

interface IAlarmDatabase {
    companion object{
        private val LOG_TAG: String? = IAlarmDatabase::class.simpleName
        private var adapter: ContactListAdapter? = null

        fun setAdapter(adapter: ContactListAdapter){
            this.adapter = adapter
        }
    }

     fun activateContact(contact: Contact, itemView: View){
        class UpdateActive: AsyncTask<Unit, Unit, List<Contact>>(){
            override fun doInBackground(vararg p0: Unit?): List<Contact> {
                val appDb: EmergencyAppDatabase = EmergencyAppDatabase.getInstance(itemView.context)
                contact.active = !contact.active
                appDb.contactDao().updateContact(contact)
                return appDb.contactDao().getAllContact()
            }

            override fun onPostExecute(result: List<Contact>?) {
                if(result != null){
                    // update Contacts adapter
                    if(adapter != null){
                        adapter?.contacts = result
                        adapter?.notifyDataSetChanged()
                    }
                }
            }
        }
        val ua = UpdateActive()
        ua.execute()
    }

    fun switchContact(clickedContact: Contact, itemView: View, upClicked: Boolean){
        class SwitchContact : AsyncTask<Unit, Unit, List<Contact>>() {

            override fun doInBackground(vararg p0: Unit?): List<Contact>? {
                val appDb: EmergencyAppDatabase = EmergencyAppDatabase.getInstance(itemView.context)

                // get Contact, which will be also switch
                val res = if(upClicked){
                    appDb.contactDao().getContactByPriority((clickedContact.priority-1))
                }else{
                    appDb.contactDao().getContactByPriority((clickedContact.priority+1))
                }

                if(res != null){
                    // switch priority of the Contacts
                    res.priority = clickedContact.priority.also { clickedContact.priority = res.priority }
                    appDb.contactDao().updateContact(res)
                    appDb.contactDao().updateContact(clickedContact)

                    // get new order of Contacts
                    return appDb.contactDao().getAllContact()
                }else{
                    return null
                }
            }

            override fun onPostExecute(result: List<Contact>?) {
                if(result != null){
                    // update Contacts adapter
                    if(adapter != null){
                        adapter?.contacts = result
                        adapter?.notifyDataSetChanged()
                    }
                }
            }
        }

        val gd = SwitchContact()
        gd.execute()
    }

    fun updateContact(contact: Contact, itemView: View){
        val intent = Intent(itemView.context, AddContactActivity::class.java)
        intent.putExtra(itemView.context.getString(R.string.numberAlarmDatabas), contact.number)
        intent.putExtra(itemView.context.getString(R.string.firstnameAlarmDatabase), contact.firstname)
        intent.putExtra(itemView.context.getString(R.string.lastnameAlarmDatabase), contact.lastname)
        intent.putExtra(itemView.context.getString(R.string.emailAlarmDatabase), contact.e_mail)
        intent.putExtra(itemView.context.getString(R.string.image), contact.pathToImage)
        intent.putExtra(itemView.context.getString(R.string.priority), contact.priority)
        intent.putExtra(itemView.context.getString(R.string.active), contact.active)
        itemView.context.startActivity(intent)
    }

    fun deleteContact(contact: Contact, itemView: View){
        class DeleteContact : AsyncTask<Unit, Unit, List<Contact>>(){
            override fun doInBackground(vararg p0: Unit?): List<Contact> {
                val appDb: EmergencyAppDatabase = EmergencyAppDatabase.getInstance(itemView.context)


                // change priority
                if(contact.priority!=2){
                    val c  =appDb.contactDao().getContactByPriority(contact.priority +1 )
                    if(c!=null){
                        c.priority = c.priority-1
                        appDb.contactDao().updateContact(c)
                    }

                    if(contact.priority!=1){
                        val c2 = appDb.contactDao().getContactByPriority(contact.priority+2)
                        if(c2 != null){
                            c2.priority = c2.priority-1
                            appDb.contactDao().updateContact(c2)
                        }
                    }
                }

                appDb.contactDao().deleteContact(contact)
                return appDb.contactDao().getAllContact()
            }

            override fun onPostExecute(result: List<Contact>?) {
                if(result!=null)
                    if(adapter != null){
                        adapter?.contacts = result
                        adapter?.notifyDataSetChanged()
                    }
            }
        }
        val dc = DeleteContact()
        dc.execute()
    }
}