package com.example.notfallapp.adapters

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.example.notfallapp.R
import com.example.notfallapp.bll.Alarm


class CustomAlarmAdapter(private var context: Activity, private var alarms: List<Alarm>) :
    ArrayAdapter<String>(context.applicationContext, R.layout.listview_item_alarm) {

    fun setAlarms(alarms: List<Alarm>){
        this.alarms=alarms
        notifyDataSetChanged()
    }

    override fun getView(position: Int, view: View, parent: ViewGroup): View {
        val inflater: LayoutInflater = LayoutInflater.from(context.applicationContext)
        val rowView: View = inflater.inflate(R.layout.listview_item_alarm, null, true)
        val txtTitle = rowView.findViewById(R.id.tvAlarmId) as TextView
        val txtName = rowView.findViewById(R.id.tvAlarmName) as TextView
        val txtTime = rowView.findViewById(R.id.tvTimeAlarm) as TextView

        txtTitle.text = alarms[position].deviceId
        txtName.text = alarms[position].deviceName
        txtTime.text = alarms[position].alertTime
        return rowView
    }

}