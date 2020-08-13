package com.example.notfallapp.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.notfallapp.R
import com.example.notfallapp.bll.Alert
import com.example.notfallapp.menubar.alert.DetailAlertActivity

/**
 * Recycler View Adapter for Alert History
 **/
class AlertsListAdapter(private var alerts: List<Alert>) : RecyclerView.Adapter<AlertsListAdapter.AlertsViewHolder>(){
    /**
     * View Holder for AlertsListAdapter
     */
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AlertsViewHolder {
        val itemView: View = LayoutInflater.from(parent.context).inflate(R.layout.recyclerview_item_alert, parent, false)
        return AlertsViewHolder(itemView)
    }

    /**
     * Bind View Holder for AlertListAdapter
     */
    override fun onBindViewHolder(holder: AlertsViewHolder, position: Int) {
        val alert: Alert? = alerts[position]
        if(alert != null){
            holder.bindAlert(alert)

            holder.itemView.setOnClickListener{
                val intent = Intent(holder.itemView.context, DetailAlertActivity::class.java)
                intent.putExtra("deviceId", alert.deviceId)
                intent.putExtra("longitude", alert.triggeringPositionLongitude)
                intent.putExtra("latitude", alert.triggeringPositionLatitude)
                intent.putExtra("timestamp", alert.date)
                intent.putExtra("accepted", alert.helperId)
                holder.itemView.context.startActivity(intent)
            }
        }
    }

    /**
     * Returns Size of alertslist
     */
    override fun getItemCount(): Int {
        return alerts.size
    }

    /**
     * class for AlertsViewHolder
     */
    class AlertsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private lateinit var alertDate: TextView
        private lateinit var alertHelper: TextView

        /**
         * Bind our Alerts
         */
        fun bindAlert(alert: Alert){
            alertDate = itemView.findViewById(R.id.alertDate)
            alertHelper = itemView.findViewById(R.id.alertHelper)

            val timestamp = alert.date.split('.')[0]
            val time = timestamp.split('T')

            alertDate.text = time[0] + " " + time[1]
            alertHelper.text = alert.helperId.toString()
        }
    }
}