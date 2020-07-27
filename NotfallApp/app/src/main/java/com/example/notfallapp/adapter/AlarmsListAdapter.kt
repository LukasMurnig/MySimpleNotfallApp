package com.example.notfallapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import com.example.notfallapp.R
import com.example.notfallapp.bll.Alarm


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
        }
    }

    override fun getItemCount(): Int {
        return alarms.size
    }

    class AlarmsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private lateinit var deviceId: TextView
        private lateinit var longitude: TextView
        private lateinit var latitude: TextView
        private lateinit var answeredContact: TextView
        private lateinit var alertTime: TextView

        fun bindAlarm(alarm: Alarm){
            deviceId = itemView.findViewById(R.id.alertId)
            longitude = itemView.findViewById(R.id.alertLongitude)
            latitude = itemView.findViewById(R.id.alertLatitude)
            answeredContact = itemView.findViewById(R.id.alertAnswerdContact)
            alertTime = itemView.findViewById(R.id.alertTime)
            deviceId.text = alarm.deviceId
            longitude.text = alarm.longitude.toString()
            latitude.text = alarm .latitude.toString()
            answeredContact.text = alarm.answeredContact
            alertTime.text = alarm.alertTime
        }
    }
}