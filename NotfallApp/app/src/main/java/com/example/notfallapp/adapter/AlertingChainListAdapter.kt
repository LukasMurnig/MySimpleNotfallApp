package com.example.notfallapp.adapter

import android.app.AlertDialog
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
import com.example.notfallapp.bll.AlertingChain
import com.example.notfallapp.bll.AlertingChainMember
import com.example.notfallapp.interfaces.IAlertingChainMemberFunctions
import kotlinx.android.synthetic.main.listcontact_item.view.*

class AlertingChainListAdapter(var alertingChain: AlertingChain) :
    RecyclerView.Adapter<AlertingChainListAdapter.AlertingChainMembersViewHolder>(){

    companion object {
        private val LOG_TAG: String? = AlertingChainListAdapter::class.simpleName
    }

    @NonNull
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AlertingChainMembersViewHolder {
        val itemView: View = LayoutInflater.from(parent.context).inflate(R.layout.listcontact_item, parent, false)
        return AlertingChainMembersViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return if(alertingChain.helpers==null){
            0
        } else {
            alertingChain.helpers!!.size
        }
    }

    override fun onBindViewHolder(
        holder: AlertingChainMembersViewHolder,
        position: Int
    ) {
        val alertingChainMember: AlertingChainMember? = alertingChain.helpers?.get(position)
        if(alertingChainMember != null){
            holder.bindAlertingChainMember(alertingChainMember)
        }
    }

    class AlertingChainMembersViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), IAlertingChainMemberFunctions {
        private lateinit var imageContact: ImageView
        private lateinit var contactName: TextView
        private lateinit var contactActive: ImageView
        private lateinit var contactMenu: ImageButton

        fun bindAlertingChainMember(alertingChainMember: AlertingChainMember){
            imageContact = itemView.findViewById(R.id.contact_item_icon)
            contactName = itemView.findViewById(R.id.contact_name)
            contactActive = itemView.findViewById(R.id.contact_active)
            contactMenu = itemView.findViewById(R.id.iBtnContactMenu)

            contactName.text = alertingChainMember.helperSurname + ", " + alertingChainMember.helperForename
            if(alertingChainMember.active){
                contactActive.setBackgroundResource(R.color.limeGreen)
                contactActive.setImageResource(R.drawable.active)
            } else {
                contactActive.setBackgroundResource(R.color.colorRed)
                contactActive.setImageResource(R.drawable.deactive)
            }

            contactMenu.setOnClickListener{
                Log.i(LOG_TAG, "AlertingChainMember Menu clicked")
                showPictureDialog(alertingChainMember, itemView)
            }
        }

        private fun showPictureDialog(alertingChainMember: AlertingChainMember, itemView: View) {
            val pictureDialog: AlertDialog.Builder = AlertDialog.Builder(itemView.context)
            val pictureDialogItems = arrayOf(
                itemView.context.resources.getString(R.string.changeValue),
                itemView.context.resources.getString(R.string.delete),
                itemView.context.resources.getString(R.string.activate_deaktivate)
            )
            pictureDialog.setItems(pictureDialogItems
            ) { _, which ->
                when (which) {
                    0 -> updateAlertingChainMember(alertingChainMember, itemView)
                    1 -> deleteAlertingChainMember(alertingChainMember, itemView)
                    2 -> {
                        if(alertingChainMember.active){
                            val builder: AlertDialog.Builder = AlertDialog.Builder(itemView.context)
                            builder.setTitle(itemView.context.resources.getString(R.string.confirm))
                            builder.setMessage(itemView.context.resources.getString(R.string.sureDeactivedContact))

                            builder.setPositiveButton(itemView.context.resources.getString(R.string.Yes)) { dialog, _ ->
                                activateAlertingChainMember(alertingChainMember, itemView)
                                dialog.dismiss()
                            }

                            builder.setNegativeButton(itemView.context.resources.getString(R.string.No)) { dialog, _ ->
                                dialog.dismiss()
                            }

                            builder.create().show()
                        }else{
                            activateAlertingChainMember(alertingChainMember, itemView)
                        }

                    }
                }
            }
            pictureDialog.show()
        }
    }
}