package com.example.notfallapp.server

import com.android.volley.Request
import com.example.notfallapp.menubar.contact.AddContactActivity
import org.json.JSONArray
import org.json.JSONObject

class ServerOrgUnitsItems {

    fun getOrgUnitItems(){
        ServerApi.createCall(Request.Method.GET, "/orgunits/${ServerApi.userId}/items", null) { response ->
            if (response.has("data")) {
                val data = response.getJSONObject("data")

                val phoneAreaCodes = data.getJSONArray("PhoneAreaCodes")
                for(i in 0 until phoneAreaCodes.length()){
                    val item = phoneAreaCodes.getJSONObject(i)
                    AddContactActivity.phoneAreaCodes =
                        AddContactActivity.phoneAreaCodes.plusElement(item.getString("Value"))
                }

                val timezones = data.getJSONArray("Timezones")
                for(i in 0 until timezones.length()){
                    val item = timezones.getJSONObject(i)
                    AddContactActivity.timezones =
                        AddContactActivity.timezones.plusElement(item.getString("Value"))
                }

                val countries = data.getJSONArray("Countries")
                for(i in 0 until countries.length()){
                    val item = countries.getJSONObject(i)
                    AddContactActivity.countries =
                        AddContactActivity.countries.plusElement(item.getString("Value"))
                }

                val languages = data.getJSONArray("Countries")
                for(i in 0 until languages.length()){
                    val item = languages.getJSONObject(i)
                    AddContactActivity.languages =
                        AddContactActivity.languages.plusElement(item.getString("Value"))
                }
            }
        }

        // Solange Server noch nicht funktioniert
        val endJson = JSONObject()
        val pJsonArray = JSONArray()
        val timJsonArray = JSONArray()
        val couJsonArray = JSONArray()
        val lanJsonArray = JSONArray()

        pJsonArray.put(JSONObject().put("Key", "US").put("Value", "+1"))
        pJsonArray.put(JSONObject().put("Key", "AT").put("Value", "+43"))
        endJson.put("PhoneAreaCodes", pJsonArray)

        timJsonArray.put(JSONObject().put("Key", "America/New_York").put("Value", "America/New_York"))
        timJsonArray.put(JSONObject().put("Key", "Europe/London").put("Value", "Europe/London"))
        endJson.put("Timezones", timJsonArray)

        couJsonArray.put(JSONObject().put("Key", "US").put("Value", "USA"))
        endJson.put("Countries", couJsonArray)

        lanJsonArray.put(JSONObject().put("Key", "de-AT").put("Value", "Deutsch (Österreich)"))
        lanJsonArray.put(JSONObject().put("Key", "en-US").put("Value", "Englisch (US)"))
        endJson.put("Languages", lanJsonArray)

        val phoneAreaCodes = endJson.getJSONArray("PhoneAreaCodes")
        for(i in 0 until phoneAreaCodes.length()){
            val item = phoneAreaCodes.getJSONObject(i)
            AddContactActivity.phoneAreaCodes =
            AddContactActivity.phoneAreaCodes.plusElement(item.getString("Value"))
        }

        val timezones = endJson.getJSONArray("Timezones")
        for(i in 0 until timezones.length()){
            val item = timezones.getJSONObject(i)
            AddContactActivity.timezones =
            AddContactActivity.timezones.plusElement(item.getString("Value"))
        }

        val countries = endJson.getJSONArray("Countries")
        for(i in 0 until countries.length()){
            val item = countries.getJSONObject(i)
            AddContactActivity.countries =
            AddContactActivity.countries.plusElement(item.getString("Value"))
        }

        val languages = endJson.getJSONArray("Countries")
        for(i in 0 until languages.length()){
            val item = languages.getJSONObject(i)
            AddContactActivity.languages =
            AddContactActivity.languages.plusElement(item.getString("Value"))
        }
    }
}