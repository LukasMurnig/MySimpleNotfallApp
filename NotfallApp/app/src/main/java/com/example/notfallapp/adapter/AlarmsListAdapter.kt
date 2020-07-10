package com.example.notfallapp.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.example.notfallapp.R
import com.example.notfallapp.bll.Alarm


class AlarmsListAdapter(context: Activity, alarms: ArrayList<Alarm>) :
    ArrayAdapter<Alarm>(context, 0) {

    companion object {
        private val LOG_TAG: String? = AlarmsListAdapter::class.simpleName
    }

    override fun getView(position: Int, convertView: View, parent: ViewGroup ): View {
        var listItemView: View? = null //convertView

        if (listItemView == null){
            listItemView = LayoutInflater.from(context).inflate(R.layout.listview_item_alarm, parent, false)
        }

        var currentAlarm: Alarm = getItem(position)


        var alarmIdTextView: TextView = listItemView?.findViewById(R.id.alertId) as TextView

        alarmIdTextView.text = currentAlarm.deviceId

        var alarmNameTextView: TextView = listItemView?.findViewById(R.id.alertName) as TextView

        alarmNameTextView.text = currentAlarm.deviceName

        var alarmTimeTextView: TextView = listItemView?.findViewById(R.id.alertTime) as TextView

        alarmTimeTextView.text = currentAlarm.alertTime

        return listItemView
}}