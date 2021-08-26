package com.example.notfallapp.server

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.example.notfallapp.adapter.AlertingChainListAdapter
import com.example.notfallapp.bll.AlertingChain
import com.example.notfallapp.bll.AlertingChainMember
import com.example.notfallapp.menubar.contact.ContactActivity
import org.json.JSONArray
import java.util.*

/**
 * class that has the function to get the AlertingChain from the Server
 */
class ServerAlertingChain {

    /**
     * function create the request, handle the response from the server and
     * fill the data to a recycler view
     */
    fun getAlertingChain(context: Context, rvContacts: RecyclerView){
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
                } else {
                    for (i in 0 until alertingChainMembers.length()) {
                        val json = alertingChainMembers.getJSONObject(i)

                        alertChM = alertChM?.plus(
                            AlertingChainMember(
                                UUID.fromString(ResponseConverter().isStringOrNull("AlertingChainId", json)),
                                UUID.fromString(ResponseConverter().isStringOrNull("HelperId", json)),
                                json.getInt("Rank"),
                                json.getBoolean("Active"),
                                json.getBoolean("Contact"),
                                ResponseConverter().isStringOrNull("HelperForename", json),
                                ResponseConverter().isStringOrNull("HelperSurname", json),
                                ResponseConverter().isStringOrNull("PhoneNumber", json),
                                ResponseConverter().isStringOrNull("Email", json)
                            )
                        )
                    }
                }
                ContactActivity.alertingChain = AlertingChain(
                    UUID.fromString(response.get("ID") as String?),
                    UUID.fromString(response.get("UserId") as String?),
                    ResponseConverter().isStringOrNull("Name", response),
                    ResponseConverter().isStringOrNull("Description", response),
                    alertChM
                )

                val adapter = AlertingChainListAdapter(ContactActivity.alertingChain!!)
                rvContacts.adapter = adapter
                (rvContacts.adapter as AlertingChainListAdapter).notifyDataSetChanged()
            }
        }
    }
}