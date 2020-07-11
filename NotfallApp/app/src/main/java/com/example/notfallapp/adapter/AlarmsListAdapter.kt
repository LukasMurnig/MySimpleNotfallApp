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
import kotlinx.android.synthetic.main.listview_item_alarm.view.*


class AlarmsListAdapter : RecyclerView.Adapter<AlarmsListAdapter.AlarmsViewHolder>{
    private lateinit var layoutInflater: LayoutInflater
    private lateinit var alarms: List<Alarm>
    private lateinit var context: Context

    fun AlarmsListAdapter(
        alarms: List<Alarm>,
        context: Context?
    ) {
        layoutInflater = LayoutInflater.from(context)
        this.alarms = alarms
        this.context = context!!
    }

    @NonNull
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AlarmsListAdapter.AlarmsViewHolder {
        val itemView: View = LayoutInflater.from(parent.context).inflate(R.layout.listview_item_alarm, parent, false)
        return AlarmsViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: AlarmsListAdapter.AlarmsViewHolder, position: Int) {
        if(alarms == null){
            return
        }

        val alarm: Alarm? = alarms[position]
        if(alarm!=null){
            holder.alertId.setText(alarm.deviceId)
            holder.alertName.setText(alarm.deviceName)
            holder.alertTime.setText(alarm.alertTime)
            holder.itemView.setOnClickListener({
                throw NotImplementedError()
            })
        }
    }

    override fun getItemCount(): Int {
        if(alarms==null){
            return 0
        }else{
            return alarms.size
        }
    }

    class AlarmsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private lateinit var deviceId: TextView
        private lateinit var deviceName: TextView
        private lateinit var alertTime: TextView

        fun AlarmsViewHolder(@NonNull itemView: View){
            super(itemView)
            deviceId = itemView.findViewById(R.id.alertId)
            deviceName = itemView.findViewById(R.id.alertName)
            alertTime = itemView.findViewById(R.id.alertTime)
        }
    }
}