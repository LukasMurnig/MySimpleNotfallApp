package com.example.notfallapp.server

import com.android.volley.Request
import com.example.notfallapp.menubar.contact.AddContactActivity

class ServerOrgUnitsItems {

    fun getOrgUnitItems(){
        ServerApi.createCall(Request.Method.GET, "/orgunits/${ServerApi.userId}/items", null) { response ->
            if (response.has("data")) {
                val data = response.getJSONObject("data")

                val phoneAreaCodes = data.getJSONArray("PhoneAreaCodes")
                for(i in 0 until phoneAreaCodes.length()){
                    val item = phoneAreaCodes.getJSONObject(i)
                    AddContactActivity.phoneAreaCodes?.plus(Pair(item.getString("Key"),item.getString("Value")))
                }

                val timezones = data.getJSONArray("Timezones")
                for(i in 0 until timezones.length()){
                    val item = timezones.getJSONObject(i)
                    AddContactActivity.timezones?.plus(Pair(item.getString("Key"),item.getString("Value")))
                }

                val countries = data.getJSONArray("Countries")
                for(i in 0 until countries.length()){
                    val item = countries.getJSONObject(i)
                    AddContactActivity.countries?.plus(Pair(item.getString("Key"),item.getString("Value")))
                }

                val languages = data.getJSONArray("Countries")
                for(i in 0 until languages.length()){
                    val item = languages.getJSONObject(i)
                    AddContactActivity.languages?.plus(Pair(item.getString("Key"),item.getString("Value")))
                }
            }
        }
    }
}