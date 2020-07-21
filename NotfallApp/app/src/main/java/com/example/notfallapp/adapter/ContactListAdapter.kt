package com.example.notfallapp.adapter

import android.content.Context
import android.net.Uri
import android.os.AsyncTask
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import com.example.notfallapp.R
import com.example.notfallapp.bll.Contact
import com.example.notfallapp.database.EmergencyAppDatabase

class ContactListAdapter(private var contacts: List<Contact>) :
    RecyclerView.Adapter<ContactListAdapter.ContactsViewHolder>() {

    private lateinit var layoutInflater: LayoutInflater
    private lateinit var context: Context

    companion object {
        private val LOG_TAG: String? = ContactListAdapter::class.simpleName
        private var adapter: ContactListAdapter? = null

        fun setAdapter(adapter: ContactListAdapter){
            this.adapter = adapter
        }
    }

    @NonNull
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ContactsViewHolder {
        val itemView: View = LayoutInflater.from(parent.context).inflate(R.layout.listcontact_item, parent, false)
        return ContactsViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ContactsViewHolder, position: Int) {
        val contact: Contact? = contacts[position]
        if(contact != null){
            holder.bindContact(contact)
        }
    }

    override fun getItemCount(): Int {
        return contacts.size
    }

    class ContactsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private lateinit var ibtnArrowUp: ImageButton
        private lateinit var ibtnArrowDown: ImageButton
        private lateinit var imageContact: ImageView
        private lateinit var contactName: TextView
        private lateinit var contactEmail: TextView

        fun bindContact(contact: Contact){
            ibtnArrowUp = itemView.findViewById(R.id.ibtnArrowUp)
            ibtnArrowDown = itemView.findViewById(R.id.ibtnArrowDown)
            imageContact = itemView.findViewById(R.id.contact_item_icon)
            contactName = itemView.findViewById(R.id.contact_name)
            contactEmail = itemView.findViewById(R.id.contact_email)

            if(contact.pathToImage.isNotEmpty()){
                val bitmap =
                    MediaStore.Images.Media.getBitmap(itemView.context.contentResolver, Uri.parse(contact.pathToImage))
                imageContact.setImageBitmap(bitmap)
            }
            contactName.text = contact.lastname + " " + contact.firstname + " " + contact.priority
            contactEmail.text = contact.e_mail

            ibtnArrowUp.setOnClickListener{
                if(contact.priority==0){
                    return@setOnClickListener
                }
                switchContact(contact, itemView, true)
            }

            ibtnArrowDown.setOnClickListener{
                if(contact.priority == 2){
                    return@setOnClickListener
                }
                switchContact(contact, itemView, false)
            }
        }

        private fun switchContact(clickedContact: Contact, itemView: View, upClicked: Boolean){
            class SwitchContact : AsyncTask<Unit, Unit, List<Contact>>() {

                override fun doInBackground(vararg p0: Unit?): List<Contact> {
                    val appDb: EmergencyAppDatabase = EmergencyAppDatabase.getInstance(itemView.context)

                    val res = if(upClicked){
                        appDb.contactDao().getContactByPriority((clickedContact.priority-1))
                    }else{
                        appDb.contactDao().getContactByPriority((clickedContact.priority+1))
                    }

                    res.priority = clickedContact.priority.also { clickedContact.priority = res.priority }
                    appDb.contactDao().updateContact(res)
                    appDb.contactDao().updateContact(clickedContact)

                    return appDb.contactDao().getAllContact()
                }

                override fun onPostExecute(result: List<Contact>?) {
                    if(result != null){
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
    }
}