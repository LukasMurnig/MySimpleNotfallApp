package com.example.notfallapp.server

import android.util.Log
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.example.notfallapp.bll.AlertingChain
import com.example.notfallapp.bll.AlertingChainMember
import com.example.notfallapp.bll.User
import org.json.JSONObject
import java.util.*
import kotlin.collections.HashMap

class ServerAlertingChain {

    fun getAlertingChain(){
        ServerApi.createCall(Request.Method.GET, "/users/${ServerApi.userId}/alertingchain/", null) { response ->
            if (response.has("data")) {
                val data = response.getJSONObject("data")
                val alertingChainMembers = data.get("Helpers") as Array<JSONObject>

                var alerChM: Array<AlertingChainMember>? = arrayOf()
                if(alertingChainMembers.isEmpty()){
                    alerChM = null
                }else{
                    for(json: JSONObject in alertingChainMembers){
                        alerChM?.plus(
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
                    alerChM
                )
            }
        }
    }
    companion object{
        fun getAlertingChain(){
            ServerApi.controlToken()

            val jsonObjectRequest: JsonObjectRequest = object : JsonObjectRequest(
                Method.GET, ServerApi.serverAPIURL+"/users/${ServerApi.userId}/alertingchain/", null,
                Response.Listener<JSONObject> { response ->
                    Log.e(ServerApi.TAG, "response: $response")
                    try {
                        val isSuccess = response.getBoolean("isSuccess")
                        val code = response.getInt("code")
                        val message = response.getString("message")
                        if (response.has("data")) {
                            // array of json?
                            val data = response.getJSONObject("data")
                            val alertingChainMembers = data.get("Helpers") as Array<JSONObject>

                            var alerChM: Array<AlertingChainMember>? = arrayOf()
                            if(alertingChainMembers.isEmpty()){
                                alerChM = null
                            }else{
                                for(json: JSONObject in alertingChainMembers){
                                    alerChM?.plus(
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
                                alerChM
                            )
                        }
                    } catch (e: Exception) { // caught while parsing the response
                        Log.e(ServerApi.TAG, "problem occurred")
                        e.printStackTrace()
                    }
                }, Response.ErrorListener { error ->
                    val resErrorBody = JSONObject(String(error.networkResponse.data))
                    Log.e(ServerApi.TAG, "problem occurred, volley error: " + error.networkResponse.statusCode + " " + resErrorBody.get("Error"))
                }) {
                @Throws(AuthFailureError::class)
                override fun getHeaders(): Map<String, String> {
                    var params: MutableMap<String, String>? = super.getHeaders()
                    if (params == null) params = HashMap()
                    params["Authorization"] = ServerApi.accessToken.toString()
                    //..add other headers
                    return params
                }
            }

            ServerApi.volleyRequestQueue?.add(jsonObjectRequest)
        }
    }
}