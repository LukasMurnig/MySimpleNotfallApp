package com.example.notfallapp.server

import com.android.volley.Request
import com.example.notfallapp.bll.AlertingChain
import com.example.notfallapp.bll.AlertingChainMember
import com.example.notfallapp.menubar.alert.AlarmsActivity
import com.example.notfallapp.menubar.contact.ContactActivity
import org.json.JSONArray
import org.json.JSONObject
import java.util.*

class ServerAlertingChain {

    fun getAlertingChain(){
        ServerApi.createCall(Request.Method.GET, "/users/${ServerApi.userId}/alertingchain/", null) { response ->
            if (response.has("data")) {
                val data = response.getJSONObject("data")
                val alertingChainMembers = data.get("Helpers") as Array<JSONObject>

                var alertChM: Array<AlertingChainMember>? = arrayOf()
                if(alertingChainMembers.isEmpty()){
                    alertChM = null
                }else{
                    for(json: JSONObject in alertingChainMembers){
                        alertChM?.plus(
                            AlertingChainMember(
                                json.get("AlertingChainId") as UUID,
                                json.get("HelperId") as UUID,
                                json.getInt("Rank"),
                                json.getBoolean("Active"),
                                json.getBoolean("Contact"),
                                json.get("HelperForename") as String?,
                                json.get("HelperSurname") as String?,
                                json.get("PhoneNumber") as String?,
                                json.get("Email") as String?
                            )
                        )
                    }
                }
                ContactActivity.alertingChain = AlertingChain(
                    data.get("ID") as UUID,
                    data.get("UserId") as UUID,
                    data.get("Name") as String?,
                    data.get("Description") as String?,
                    alertChM
                )
            }
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