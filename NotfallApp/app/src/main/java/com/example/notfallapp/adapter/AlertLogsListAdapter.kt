package com.example.notfallapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.notfallapp.R
import com.example.notfallapp.bll.AlertLog

class AlertLogsListAdapter(private var alertLogs: List<AlertLog>) : RecyclerView.Adapter<AlertLogsListAdapter.AlertLogsViewHolder>(){
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AlertLogsListAdapter.AlertLogsViewHolder {
        val itemView: View = LayoutInflater.from(parent.context).inflate(R.layout.recyclerview_item_alertlog, parent, false)
        return AlertLogsViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: AlertLogsListAdapter.AlertLogsViewHolder, position: Int) {
        val alertLog: AlertLog? = alertLogs[position]
        if(alertLog != null){
            holder.bindAlertLog(alertLog)
        }
    }

    override fun getItemCount(): Int {
        return alertLogs.size
    }

    class AlertLogsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private lateinit var alertLogDate: TextView
        private lateinit var alertLogUser: TextView
        private lateinit var alertLogComment: TextView

        fun bindAlertLog(alertLog: AlertLog) {
            alertLogDate = itemView.findViewById(R.id.alertLogDate)
            alertLogUser = itemView.findViewById(R.id.alertLogUser)
            alertLogComment = itemView.findViewById(R.id.alertLogComment)

            val timestamp = alertLog.date.split('.')[0]
            var time = timestamp.split('T')
            val date = time[0].split('-')
            time = time[1].split(':')

            alertLogDate.text = "${date[2]}.${date[1]}.${date[0]} ${time[0]}:${time[1]}"
            alertLogUser.text = alertLog.userId.toString()

            when(alertLog.logType){
                0 -> alertLogComment.text = "Alert triggered"
                1 -> alertLogComment.text = "Alert accepted"
                2 -> alertLogComment.text = "Alert invalidated"
                3 -> alertLogComment.text = "Alert Closed"
                4 -> alertLogComment.text = " Close Forced by system"
                5 -> alertLogComment.text = "Close Forced by movement zone"
                6 -> alertLogComment.text = "Close Forced by wlan connection"
                10 -> alertLogComment.text = "User contacted"
                11 -> alertLogComment.text = "User status recall (deprecated)"
                12 -> alertLogComment.text = "User Contacted via sms"
                13 -> alertLogComment.text = "User Contacted via email"
                14 -> alertLogComment.text = "Call Center contacted"
                20 -> alertLogComment.text = "User not reachable"
                21 -> alertLogComment.text = "User absent"
                22 -> alertLogComment.text = "User inactive"
                23 -> alertLogComment.text = "User phone number missing"
                24 -> alertLogComment.text = "User no contact type activated"
                25 -> alertLogComment.text = "User already alerted"
                30 -> alertLogComment.text = "User refused"
                31 -> alertLogComment.text = "User hang up"
                40 -> alertLogComment.text = "alert restart chain"
                41 -> alertLogComment.text = "alerting chain completed"
                50 -> {
                    if(alertLog.message != null){
                        alertLogComment.text = alertLog.message
                    }
                }
            }
        }
    }
}