package com.example.notfallapp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import com.example.notfallapp.R
import com.example.notfallapp.bll.Alarm
import com.example.notfallapp.bll.Contact

class ContactListAdapter(private var contacts: List<Contact>) :
    RecyclerView.Adapter<ContactListAdapter.ContactsViewHolder>() {

    private lateinit var layoutInflater: LayoutInflater
    private lateinit var context: Context

    companion object {
        private val LOG_TAG: String? = ContactListAdapter::class.simpleName
    }

    @NonNull
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ContactListAdapter.ContactsViewHolder {
        val itemView: View = LayoutInflater.from(parent.context).inflate(R.layout.listcontact_item, parent, false)
        return ContactListAdapter.ContactsViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ContactListAdapter.ContactsViewHolder, position: Int) {
        val contact: Contact? = contacts[position]
        if(contact!=null){
            holder.bindContact(contact)
        }
    }

    override fun getItemCount(): Int {
        return contacts.size
    }

    class ContactsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private lateinit var contactName: TextView
        private lateinit var contactEmail: TextView

        fun bindContact(contact: Contact){
            contactName = itemView.findViewById(R.id.contact_name)
            contactEmail = itemView.findViewById(R.id.contact_email)

            contactName.text = contact.lastname  +" "+contact.firstname
            contactEmail.text = contact.e_mail
        }
    }

}