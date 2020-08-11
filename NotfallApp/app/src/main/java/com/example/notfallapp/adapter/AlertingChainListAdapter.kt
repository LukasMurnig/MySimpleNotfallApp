package com.example.notfallapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import com.example.notfallapp.R
import com.example.notfallapp.bll.AlertingChain
import com.example.notfallapp.bll.AlertingChainMember

/*
 * RecyclerView Adapter for the Alerting Chain
 */
class AlertingChainListAdapter(private var alertingChain: AlertingChain) :
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
        return if(alertingChain.helpers == null){
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

    class AlertingChainMembersViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        //private lateinit var imageContact: ImageView
        private lateinit var contactName: TextView
        private lateinit var contactActive: ImageView

        fun bindAlertingChainMember(alertingChainMember: AlertingChainMember){
            //imageContact = itemView.findViewById(R.id.contact_item_icon)
            contactName = itemView.findViewById(R.id.contact_name)
            contactActive = itemView.findViewById(R.id.contact_active)

            contactName.text = alertingChainMember.helperSurname + ", " + alertingChainMember.helperForename
            if(alertingChainMember.active){
                contactActive.setBackgroundResource(R.color.limeGreen)
                contactActive.setImageResource(R.drawable.active)
            } else {
                contactActive.setBackgroundResource(R.color.colorRed)
                contactActive.setImageResource(R.drawable.deactive)
            }

        }
    }
}