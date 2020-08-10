package com.example.notfallapp.server

import android.content.Context
import android.util.Log
import com.android.volley.Request
import com.example.notfallapp.bll.AlertingChain
import com.example.notfallapp.bll.AlertingChainMember
import com.example.notfallapp.menubar.contact.ContactActivity
import org.json.JSONArray
import org.json.JSONObject
import java.util.*

class ServerAlertingChain {

    fun getAlertingChain(context: Context){
        val pref = context.getSharedPreferences("Response", Context.MODE_PRIVATE)
        val userId = pref.getString("UserId", null) ?: return

        ServerApi.createJsonObjectRequest(Request.Method.GET, "/users/$userId/alertingchain/", null) { response ->
            if (response.has("ID")) {
                var alertingChainMembers: JSONArray? = null

                if(response.getJSONArray("Helpers") != null){
                    alertingChainMembers = response.getJSONArray("Helpers")
                }

                var alertChM: Array<AlertingChainMember>? = arrayOf()
                if(alertingChainMembers == null){
                    alertChM = null
                }else{
                    for (i in 0 until alertingChainMembers.length()) {
                        val json = alertingChainMembers.getJSONObject(i)

                        alertChM = alertChM?.plus(
                            AlertingChainMember(
                                UUID.fromString(json.get("AlertingChainId") as String?),
                                UUID.fromString(json.get("HelperId") as String?),
                                json.getInt("Rank"),
                                json.getBoolean("Active"),
                                json.getBoolean("Contact"),
                                proveIfNullOrValue("HelperForename", json) as String?,
                                proveIfNullOrValue("HelperSurname", json) as String?,
                                proveIfNullOrValue("PhoneNumber", json) as String?,
                                proveIfNullOrValue("Email", json) as String?
                            )
                        )
                    }
                }
                ContactActivity.alertingChain = AlertingChain(
                    UUID.fromString(response.get("ID") as String?),
                    UUID.fromString(response.get("UserId") as String?),
                    isStringOrNull("Name", response),
                    isStringOrNull("Description", response),
                    alertChM
                )
                Log.e("ServerAlertingChain", "AlertingChain set")
            }
        }
    }

    private fun isStringOrNull(key: String, response: JSONObject): String? {
        return if(response.getString(key) == null){
            null
        } else {
            response.getString(key)
        }
    }

    private fun proveIfNullOrValue(key: String, response: JSONObject): Any?{
        return try{
            response.get(key)
        }catch (ex: Exception){
            null
        }
    }

    fun updateAlertingChainMembers(alertingChainMembers: Array<AlertingChainMember>){
        val reqBody = JSONObject()
        val helpers = JSONArray()

        alertingChainMembers.forEach { member ->
            val jsonMember = JSONObject()
            jsonMember.put("AlertingChainId", member.alertingChainId)
            jsonMember.put("HelperId", member.helperId)
            jsonMember.put("Rank", member.rank)
            jsonMember.put("Active", member.active)
            jsonMember.put("Contact", member.contact)
            jsonMember.put("HelperForename", member.helperForename)
            jsonMember.put("HelperSurname", member.helperSurname)
            jsonMember.put("PhoneNumber", member.phoneNumber)
            jsonMember.put("Email", member.email)
            helpers.put(jsonMember)
        }

        reqBody.put("Helpers", helpers)

        ServerApi.createJsonObjectRequest(Request.Method.PUT, "/users/${ServerApi.userId}/alertingchain/", reqBody){response ->
            if (response.has("data")) {
                val data = response.getJSONObject("data")
                // TODO update alertingChain data in Contact Activity
            }
        }
    }
}