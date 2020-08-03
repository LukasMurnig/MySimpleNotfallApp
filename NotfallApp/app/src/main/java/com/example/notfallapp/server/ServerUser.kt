package com.example.notfallapp.server

import android.content.Context
import com.android.volley.Request
import com.example.notfallapp.R
import com.example.notfallapp.bll.User
import com.example.notfallapp.menubar.settings.SettingsActivity
import com.example.notfallapp.server.ServerApi.Companion.createCall
import org.json.JSONObject
import java.util.*

class ServerUser {

    fun getUserInfo(context: Context){
        createCall(Request.Method.GET, context.getString(R.string.UserMe), null ){ response ->
            if (response.has("data")) {
                val data = response.getJSONObject("data")
                SettingsActivity.logInUser = User(
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
        // solange Server noch nicht funktioniert
        SettingsActivity.logInUser = User(
            UUID(13215,123),
            null,
            "Dr",
            "Maria",
            "Musterfrau",
            "Mamufrau",
            true,
            "User",
             false,
            true,
            Date(),
            "maria.muster@mail.com",
            "06769392808",
            1,
            "Deutsch (Österreich)",
            null)
    }

    fun updateUserInfo(user: User){
        val reqBody = JSONObject()
        reqBody.put("ForeignId", user.foreignId)
        reqBody.put("Title", user.title)
        reqBody.put("Forename", user.forename)
        reqBody.put("Surname", user.surname)
        reqBody.put("Active", user.active)
        reqBody.put("Role", user.role)
        reqBody.put("Gender", user.gender)
        reqBody.put("BirthDay", user.birthDay)
        reqBody.put("EmailAddress", user.emailAddress)
        reqBody.put("PhoneFixed", user.phoneFixed)
        reqBody.put("OrgUnit", user.orgUnit)
        reqBody.put("Language", user.language)
        reqBody.put("TimeZone", user.timezone)

        createCall(Request.Method.PUT, "/users/${ServerApi.userId}", reqBody){ response ->
            if (response.has("data")) {
                val data = response.getJSONObject("data")
                // TODO update user data in settings Activity
            }
        }
    }
}