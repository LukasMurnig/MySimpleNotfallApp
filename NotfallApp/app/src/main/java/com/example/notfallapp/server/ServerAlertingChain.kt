package com.example.notfallapp.server

import com.android.volley.Request
import com.example.notfallapp.bll.AlertingChain
import com.example.notfallapp.bll.AlertingChainMember
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
                AlertingChain(
                    data.get("ID") as UUID,
                    data.get("UserId") as UUID,
                    data.get("Name") as String?,
                    data.get("Description") as String?,
                    alertChM
                )
            }
        }
    }
}