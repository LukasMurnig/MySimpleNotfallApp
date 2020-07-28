package com.example.notfallapp.server

import android.content.Context
import com.android.volley.Request
import com.example.notfallapp.R
import com.example.notfallapp.bll.User
import com.example.notfallapp.server.ServerApi.Companion.createCall
import java.util.*

class ServerUser {

    fun getUserInfo(context: Context){
        createCall(Request.Method.GET, context.getString(R.string.UserMe), null ){ response ->
            if (response.has("data")) {
                val data = response.getJSONObject("data")
                User(
                    data.get("ID") as UUID,
                    data.get("ForeignId") as String?,
                    data.get("Title") as String?,
                    data.getString("Forename"),
                    data.getString("Surename"),
                    data.getString("Username"),
                    data.getBoolean("Active"),
                    data.getString("Role"),
                    data.getBoolean("Gender"),
                    data.getBoolean("PhotoSet"),
                    data.get("BirthDay") as Date?,
                    data.get("EmailAddress") as String?,
                    data.get("PhoneFixed") as String?,
                    data.getInt("OrgUnit"),
                    data.get("Language") as String?,
                    data.get("Timezone") as String?)
            }
        }
    }
}