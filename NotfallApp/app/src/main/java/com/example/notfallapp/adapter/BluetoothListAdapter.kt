package com.example.notfallapp.adapter

import android.bluetooth.BluetoothDevice
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.example.notfallapp.R

class BluetoothListAdapter(context: Context?, devices: List<BluetoothDevice>) : ArrayAdapter<BluetoothDevice>(context!!, R.layout.listview_item_bluetoothdevices, devices){

    private lateinit var inflater: LayoutInflater
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val rowView: View = inflater.inflate(R.layout.listview_item_bluetoothdevices, parent, false)
        val name: TextView = rowView.findViewById(R.id.deviceName)
        val device: BluetoothDevice? = getItem(position)
        name.text = device?.name.toString()
        return rowView
    }
}