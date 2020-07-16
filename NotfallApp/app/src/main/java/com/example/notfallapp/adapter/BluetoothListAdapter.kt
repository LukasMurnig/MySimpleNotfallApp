package com.example.notfallapp.adapter

import android.bluetooth.BluetoothDevice
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.example.notfallapp.R

class BluetoothListAdapter(context: Context?, devices: List<BluetoothDevice>) : ArrayAdapter<BluetoothDevice>(context, R.layout.listview_item_bluetoothdevices, devices){

    private lateinit var inflater: LayoutInflater
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        System.out.println("Hello 1")
        inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        var rowView: View = inflater.inflate(R.layout.listview_item_bluetoothdevices, parent, false)
        var address: TextView = rowView.findViewById(R.id.deviceMACAddress)
        var name: TextView = rowView.findViewById(R.id.deviceName)
        var device: BluetoothDevice = getItem(position)
        address.text = device.address.toString()
        name.text = device.name.toString()
        return rowView
    }
}