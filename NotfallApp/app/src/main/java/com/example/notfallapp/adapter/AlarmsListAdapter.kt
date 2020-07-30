package com.example.notfallapp.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import com.example.notfallapp.R
import com.example.notfallapp.bll.Alarm
import com.example.notfallapp.menubar.alert.DetailAlertActivity


class AlarmsListAdapter(private var alarms: List<Alarm>) : RecyclerView.Adapter<AlarmsListAdapter.AlarmsViewHolder>(){

    @NonNull
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AlarmsViewHolder {
        val itemView: View = LayoutInflater.from(parent.context).inflate(R.layout.listview_item_alarm, parent, false)
        return AlarmsViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: AlarmsViewHolder, position: Int) {
        val alarm: Alarm? = alarms[position]
        if(alarm != null){
            holder.bindAlarm(alarm)

            holder.itemView.setOnClickListener{
                val intent = Intent(holder.itemView.context, DetailAlertActivity::class.java)
                intent.putExtra("deviceId", alarm.deviceId)
                intent.putExtra("longitude", alarm.longitude)
                intent.putExtra("latitude", alarm.latitude)
                intent.putExtra("timestamp", alarm.alertTime)
                holder.itemView.context.startActivity(intent)
            }
        }
    }

    override fun getItemCount(): Int {
        return alarms.size
    }

    class AlarmsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private lateinit var answeredContact: TextView
        private lateinit var alertTime: TextView

        fun bindAlarm(alarm: Alarm){
            answeredContact = itemView.findViewById(R.id.alertAnswerdContact)
            alertTime = itemView.findViewById(R.id.alertTime)

            answeredContact.text = alarm.answeredContact
            alertTime.text = alarm.alertTime
        }
    }
}