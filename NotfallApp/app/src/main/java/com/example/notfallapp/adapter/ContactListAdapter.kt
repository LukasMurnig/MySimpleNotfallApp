package com.example.notfallapp.adapter

import android.app.AlertDialog
import android.content.Context
import android.net.Uri
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
import com.example.notfallapp.interfaces.IAlarmDatabase
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class ContactListAdapter(var contacts: List<Contact>) :
    RecyclerView.Adapter<ContactListAdapter.ContactsViewHolder>(){

    private lateinit var layoutInflater: LayoutInflater
    private lateinit var context: Context

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

    class ContactsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), IAlarmDatabase {
        private lateinit var iBtnArrowUp: ImageButton
        private lateinit var iBtnArrowDown: ImageButton
        private lateinit var imageContact: ImageView
        private lateinit var contactName: TextView
        private lateinit var contactEmail: TextView

        private lateinit var contactMenu: ImageButton
        private lateinit var tvActive: TextView

        fun bindContact(contact: Contact){
            iBtnArrowUp = itemView.findViewById(R.id.ibtnArrowUp)
            iBtnArrowDown = itemView.findViewById(R.id.ibtnArrowDown)
            imageContact = itemView.findViewById(R.id.contact_item_icon)
            contactName = itemView.findViewById(R.id.contact_name)
            contactEmail = itemView.findViewById(R.id.contact_email)
            contactMenu = itemView.findViewById(R.id.iBtnContactMenu)
            tvActive = itemView.findViewById(R.id.tvActive)

            MainScope().launch {
                if(contact.pathToImage.isNotEmpty()){
                    val bitmap =
                        MediaStore.Images.Media.getBitmap(itemView.context.contentResolver, Uri.parse(contact.pathToImage))
                    imageContact.setImageBitmap(bitmap)
                }
            }

            contactName.text = contact.lastname + " " + contact.firstname + " " + contact.priority
            contactEmail.text = contact.e_mail
            tvActive.text = contact.active.toString()

            iBtnArrowUp.setOnClickListener{
                if(contact.priority==0){
                    return@setOnClickListener
                }

                switchContact(contact, itemView, true)
            }

            iBtnArrowDown.setOnClickListener{
                if(contact.priority == 2){
                    return@setOnClickListener
                }
                switchContact(contact, itemView, false)
            }

            contactMenu.setOnClickListener{
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
                    2 -> activateContact(contact, itemView)
                }
            }
            pictureDialog.show()
        }
    }
}