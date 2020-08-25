package com.example.notfallapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.notfallapp.R
import com.example.notfallapp.bll.AlertLog
import com.example.notfallapp.server.ServerUser
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class AlertLogsListAdapter(private var alertLogs: List<AlertLog>) : RecyclerView.Adapter<AlertLogsListAdapter.AlertLogsViewHolder>(){
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AlertLogsViewHolder {
        val itemView: View = LayoutInflater.from(parent.context).inflate(R.layout.recyclerview_item_alertlog, parent, false)
        return AlertLogsViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: AlertLogsViewHolder, position: Int) {
        val alertLog: AlertLog? = alertLogs[position]
        if(alertLog != null){
            holder.bindAlertLog(alertLog)
        }
    }

    override fun getItemCount(): Int {
        return alertLogs.size
    }

    class AlertLogsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private lateinit var tvAlertLogDate: TextView
        private lateinit var tvAlertLogUser: TextView
        private lateinit var tvAlertLogComment: TextView

        fun bindAlertLog(alertLog: AlertLog) {
            tvAlertLogDate = itemView.findViewById(R.id.alertLogDate)
            tvAlertLogUser = itemView.findViewById(R.id.alertLogUser)
            tvAlertLogComment = itemView.findViewById(R.id.alertLogComment)

            val timestamp = alertLog.date.split('.')[0]
            var time = timestamp.split('T')
            val date = time[0].split('-')
            time = time[1].split(':')

            tvAlertLogDate.text = "${date[2]}.${date[1]}.${date[0]} ${time[0]}:${time[1]}"

            tvAlertLogUser.text = alertLog.userId.toString()

            MainScope().launch {
                ServerUser().getUserName(alertLog.userId.toString(), tvAlertLogUser)
            }

            when(alertLog.logType){
                0 -> tvAlertLogComment.text = itemView.context.getString(R.string.alertLog_AlertTriggered)
                1 -> tvAlertLogComment.text = itemView.context.getString(R.string.alertLog_AlertAccepted)
                2 -> tvAlertLogComment.text = itemView.context.getString(R.string.alertLog_AlertInvalidated)
                3 -> tvAlertLogComment.text = itemView.context.getString(R.string.alertLog_AlertClosed)
                4 -> tvAlertLogComment.text = itemView.context.getString(R.string.alertLog_CloseForcedBySystem)
                5 -> tvAlertLogComment.text = itemView.context.getString(R.string.alertLog_CloseForcedByMovementZone)
                6 -> tvAlertLogComment.text = itemView.context.getString(R.string.alertLog_CloseForcedByWlanConnection)
                10 -> tvAlertLogComment.text = itemView.context.getString(R.string.alertLog_UserContacted)
                11 -> tvAlertLogComment.text = itemView.context.getString(R.string.alertLog_UserStatusRecallDeprecated)
                12 -> tvAlertLogComment.text = itemView.context.getString(R.string.alertLog_UserContactedViaSms)
                13 -> tvAlertLogComment.text = itemView.context.getString(R.string.alertLog_UserContactedViaEmail)
                14 -> tvAlertLogComment.text = itemView.context.getString(R.string.alertLog_CallCenterContacted)
                20 -> tvAlertLogComment.text = itemView.context.getString(R.string.alertLog_UserNotReachable)
                21 -> tvAlertLogComment.text = itemView.context.getString(R.string.alertLog_UserAbsent)
                22 -> tvAlertLogComment.text = itemView.context.getString(R.string.alertLog_UserInactive)
                23 -> tvAlertLogComment.text = itemView.context.getString(R.string.alertLog_UserPhoneNumberMissing)
                24 -> tvAlertLogComment.text = itemView.context.getString(R.string.alertLog_UserNoContactTypeActivated)
                25 -> tvAlertLogComment.text = itemView.context.getString(R.string.alertLog_UserAlreadyAlerted)
                30 -> tvAlertLogComment.text = itemView.context.getString(R.string.alertLog_UserRefused)
                31 -> tvAlertLogComment.text = itemView.context.getString(R.string.alertLog_UserHangUp)
                40 -> tvAlertLogComment.text = itemView.context.getString(R.string.alertLog_alertRestartChain)
                41 -> tvAlertLogComment.text = itemView.context.getString(R.string.alertLog_alertingChainCompleted)
                50 -> {
                    if(alertLog.message != null){
                        tvAlertLogComment.text = alertLog.message
                    }
                }
            }
        }
    }
}