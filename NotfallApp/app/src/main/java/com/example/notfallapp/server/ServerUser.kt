package com.example.notfallapp.server

import android.content.Context
import android.util.Log
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.example.notfallapp.R
import com.example.notfallapp.bll.User
import org.json.JSONObject
import java.util.*
import kotlin.collections.HashMap

class ServerUser {
    fun getUserInfo(context: Context): User? {

        ServerApi.controlToken()

        val jsonObjectRequest: JsonObjectRequest = object : JsonObjectRequest(
            Method.GET, ServerApi.serverAPIURL+context.getString(R.string.UserMe), null,
            Response.Listener<JSONObject> { response ->
                Log.e(ServerApi.TAG,
                      String.format(context.getString(R.string.responseMessage), response.toString()))
                try {
                    val isSuccess = response.getBoolean(context.getString(R.string.isSuccess))
                    val code = response.getInt(context.getString(R.string.code))
                    val message = response.getString(context.getString(R.string.messageServer))
                    if (response.has(context.getString(R.string.dataServer))) {
                        // array of json?
                        val data = response.getJSONObject(context.getString(R.string.dataServer))
                        val user = User(
                            data.get(context.getString(R.string.id)) as UUID,
                            data.get(context.getString(R.string.foreignId)) as String?,
                            data.get(context.getString(R.string.title)) as String?,
                            data.getString(context.getString(R.string.Forename)),
                            data.getString(context.getString(R.string.Surename)),
                            data.getString(context.getString(R.string.usernameServer)),
                            data.getBoolean(context.getString(R.string.activeServer)),
                            data.getString(context.getString(R.string.roleServer)),
                            data.getBoolean(context.getString(R.string.genderServer)),
                            data.getBoolean(context.getString(R.string.photoSetServer)),
                            data.get(context.getString(R.string.photoSetServer)) as Date?,
                            data.get(context.getString(R.string.emailAddressServer)) as String?,
                            data.get(context.getString(R.string.phoneFixedServer)) as String?,
                            data.getInt(context.getString(R.string.OrgUnitSever)),
                            data.get(context.getString(R.string.LanguageServer)) as String?,
                            data.get(context.getString(R.string.TimezoneServer)) as String?
                        )

                    }
                } catch (e: Exception) { // caught while parsing the response
                    Log.e(ServerApi.TAG,
                          String.format(context.getString(R.string.ErrorServerUserMessage),
                                        context.getString(R.string.ServerUser), e.toString()))
                    e.printStackTrace()
                }
            }, Response.ErrorListener { error ->
                val resErrorBody = JSONObject(String(error.networkResponse.data))
                Log.e(ServerApi.TAG,
                    String.format(context.getString(R.string.ErrorServerUserResponseMessage),
                        error.networkResponse.statusCode , resErrorBody.get("Error")))
            }) {
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                var params: MutableMap<String, String>? = super.getHeaders()
                if (params == null) params = HashMap()
                params[context.getString(R.string.authorizationServer)] = ServerApi.accessToken.toString()
                //..add other headers
                return params
            }
        }


        ServerApi.volleyRequestQueue?.add(jsonObjectRequest)

        return null
    }
}