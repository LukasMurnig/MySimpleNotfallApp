package com.example.notfallapp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.notfallapp.R
import com.example.notfallapp.bll.Alert
import org.w3c.dom.Text

class AlertsListAdapter(private var alerts: List<Alert>) : RecyclerView.Adapter<AlertsListAdapter.AlertsViewHolder>(){
    private lateinit var layoutInflater: LayoutInflater
    private lateinit var context: Context

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AlertsListAdapter.AlertsViewHolder {
        val itemView: View = LayoutInflater.from(parent.context).inflate(R.layout.recyclerview_item_alert, parent, false)
        return AlertsViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: AlertsViewHolder, position: Int) {
        val alert: Alert? = alerts[position]
        if(alert!=null){
            holder.bindAlert(alert)
        }
    }

    override fun getItemCount(): Int {
        return alerts.size
    }

    class AlertsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private lateinit var alertDate: TextView
        private lateinit var alertHelper: TextView
        private lateinit var alertLatitude: TextView
        private lateinit var alertLongitude: TextView

        fun bindAlert(alert: Alert){
            alertDate = itemView.findViewById(R.id.alertDate)
            alertHelper = itemView.findViewById(R.id.alertHelper)
            alertLatitude = itemView.findViewById(R.id.alertLatitude)
            alertLongitude = itemView.findViewById(R.id.alertLongitude)

            alertDate.text = alert.date.toString()
            alertHelper.text = alert.helperId.toString()
            alertLatitude.text = alert.triggeringPositionLatitude.toString()
            alertLongitude.text = alert.triggeringPositionLongitude.toString()
        }
    }



}