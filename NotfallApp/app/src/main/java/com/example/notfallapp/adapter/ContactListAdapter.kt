package com.example.notfallapp.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.example.notfallapp.R
import com.example.notfallapp.bll.Contact

class ContactListAdapter(context: Activity, contacts: ArrayList<Contact>) :
    ArrayAdapter<Contact>(context, 0) {

    companion object {
        private val LOG_TAG: String? = ContactListAdapter::class.simpleName
    }

    override fun getView(position: Int, convertView: View, parent: ViewGroup ): View {
        var listItemView: View = convertView

        if (listItemView == null){
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.activity_contacts, parent, false)
        }

        var currentContact: Contact =getItem(position)

        var nameTextView: TextView = listItemView.findViewById(R.id.contact_name) as TextView

        nameTextView.setText(currentContact.firstname + " " + currentContact.lastname)

        var emailTextView: TextView = listItemView.findViewById(R.id.contact_email) as TextView

        emailTextView.setText(currentContact.email)

        var picture: ImageView = listItemView.findViewById(R.id.contact_item_icon) as ImageView

        picture.setImageResource(currentContact.imageResourceId)

        return listItemView
    }
}