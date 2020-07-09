package com.example.notfallapp.adapter

import android.app.Activity
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.example.notfallapp.R
import com.example.notfallapp.bll.Alarm
import com.example.notfallapp.bll.Contact


class AlarmsListAdapter(context: Activity, alarms: List<Alarm>) :
    ArrayAdapter<Alarm>(context, 0) {

    companion object {
        private val LOG_TAG: String? = AlarmsListAdapter::class.simpleName
    }

    override fun getView(position: Int, convertView: View, parent: ViewGroup ): View {
        var listItemView: View = convertView

        if (listItemView == null){
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.activity_alarms, parent, false)
        }

        var currentAlarm: Alarm =getItem(position)

        var alarmIdTextView: TextView = listItemView.findViewById(R.id.alertId) as TextView

        alarmIdTextView.setText(currentAlarm.deviceId)

        var alarmNameTextView: TextView = listItemView.findViewById(R.id.alertName) as TextView

        alarmNameTextView.setText(currentAlarm.deviceName)

        var alarmTimeTextView: TextView = listItemView.findViewById(R.id.alertTime) as TextView

        alarmTimeTextView.setText(currentAlarm.alertTime)

        return listItemView
}}