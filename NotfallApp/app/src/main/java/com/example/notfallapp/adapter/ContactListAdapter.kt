package com.example.notfallapp.adapter

import android.app.AlertDialog
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
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
import com.example.notfallapp.interfaces.IContactDatabase
import kotlinx.android.synthetic.main.listcontact_item.view.*
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class ContactListAdapter(var contacts: List<Contact>) :
    RecyclerView.Adapter<ContactListAdapter.ContactsViewHolder>(){

    companion object {
        private val LOG_TAG: String? = ContactListAdapter::class.simpleName
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

    class ContactsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), IContactDatabase {
        private lateinit var imageContact: ImageView
        private lateinit var contactName: TextView
        private lateinit var contactActive: ImageView
        private lateinit var contactMenu: ImageButton

        fun bindContact(contact: Contact){
            imageContact = itemView.findViewById(R.id.contact_item_icon)
            contactName = itemView.findViewById(R.id.contact_name)
            contactActive = itemView.findViewById(R.id.contact_active)
            contactMenu = itemView.findViewById(R.id.iBtnContactMenu)

            MainScope().launch {
                if(contact.photoSet){
                    if(contact.pathToImage != null && contact.pathToImage!!.isNotEmpty()){
                        val bitmap =
                            MediaStore.Images.Media.getBitmap(itemView.context.contentResolver, Uri.parse(contact.pathToImage))
                        imageContact.setImageBitmap(bitmap)
                    }
                }
            }

            contactName.text = contact.surname + ", " + contact.forename
            if(contact.active){
                contactActive.setImageResource(R.drawable.active)
            }

            contactMenu.setOnClickListener{
                Log.i(LOG_TAG, "Contact Menu clicked")
                showPictureDialog(contact, itemView)
            }
        }

        private fun showPictureDialog(contact: Contact, itemView: View) {
            val pictureDialog: AlertDialog.Builder = AlertDialog.Builder(itemView.context)
            val pictureDialogItems = arrayOf(
                itemView.context.resources.getString(R.string.changeValue),
                itemView.context.resources.getString(R.string.delete),
                itemView.context.resources.getString(R.string.activate_deaktivate)
            )
            pictureDialog.setItems(pictureDialogItems
            ) { _, which ->
                when (which) {
                    0 -> updateContact(contact, itemView)
                    1 -> deleteContact(contact, itemView)
                    2 -> {
                        if(contact.active){
                            itemView.contact_active.setImageDrawable(null)
                        }
                        activateContact(contact, itemView)
                    }
                }
            }
            pictureDialog.show()
        }
    }
}