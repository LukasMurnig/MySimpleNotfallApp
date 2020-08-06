package com.example.notfallapp.server

import android.util.Log
import com.android.volley.Request
import com.example.notfallapp.bll.AlertingChain
import com.example.notfallapp.bll.AlertingChainMember
import com.example.notfallapp.menubar.contact.ContactActivity
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.util.*

class ServerAlertingChain {

    fun getAlertingChain(){
        while (ServerApi.userId == null){
        }
        ServerApi.createCall(Request.Method.GET, "/users/${ServerApi.userId}/alertingchain/", null) { response ->
            if (response.has("data")) {
                val data = response.getJSONObject("data")
                var alertingChainMembers: JSONArray? = null

                if(data.getJSONArray("Helpers") != null){
                    alertingChainMembers = data.getJSONArray("Helpers")
                }

                var alertChM: Array<AlertingChainMember>? = arrayOf()
                if(alertingChainMembers == null){
                    alertChM = null
                }else{
                    for (i in 0 until alertingChainMembers!!.length()) {
                        val json = alertingChainMembers!!.getJSONObject(i)

                        alertChM = alertChM?.plus(
                            AlertingChainMember(
                                json.get("AlertingChainId") as UUID,
                                json.get("HelperId") as UUID,
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
                    data.get("ID") as UUID,
                    data.get("UserId") as UUID,
                    proveIfNullOrValue("Name", data) as String?,
                    proveIfNullOrValue("Description", data) as String?,
                    alertChM
                )
                Log.e("ServerAlertingChain", "AlertingChain set")
            }
        }

        /*val endJson = JSONObject()
        endJson.put("ID", UUID(32453,34))
        endJson.put("UserId", UUID(31432,432))
        endJson.put("Name", "Test AlertingChain")
        endJson.put("Description", null)
        val jsArray = JSONArray()
        val j = JSONObject()
        j.put("AlertingChainId", UUID(32453,34))
        j.put("HelperId", UUID(32453,34))
        j.put("Rank", 1)
        j.put("Active", true)
        j.put("Contact", false)
        j.put("HelperForename", "Benni")
        j.put("HelperSurname", "Hacksteiner")
        j.put("PhoneNumber", "06769392808")
        j.put("Email", null)
        jsArray.put(j)
        endJson.put("Helpers", jsArray)

        var alertingChainMembers: JSONArray? = null

        if(endJson.getJSONArray("Helpers")!=null){
            alertingChainMembers = endJson.getJSONArray("Helpers")
        }

        var alertChM: Array<AlertingChainMember>? = arrayOf()
        if(alertingChainMembers == null){
            alertChM = null
        }else{
            for (i in 0 until alertingChainMembers!!.length()) {
                val json = alertingChainMembers!!.getJSONObject(i)

                alertChM = alertChM?.plus(
                    AlertingChainMember(
                        json.get("AlertingChainId") as UUID,
                        json.get("HelperId") as UUID,
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
            endJson.get("ID") as UUID,
            endJson.get("UserId") as UUID,
            proveIfNullOrValue("Name", endJson) as String?,
            proveIfNullOrValue("Description", endJson) as String?,
            alertChM
        )*/
    }

    private fun proveIfNullOrValue(key: String, response: JSONObject): Any?{
        return try{
            response.get(key)
        }catch (ex: JSONException){
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

        ServerApi.createCall(Request.Method.PUT, "/users/${ServerApi.userId}/alertingchain/", reqBody){response ->
            if (response.has("data")) {
                val data = response.getJSONObject("data")
                // TODO update alertingChain data in Contact Activity
            }
        }
    }
}