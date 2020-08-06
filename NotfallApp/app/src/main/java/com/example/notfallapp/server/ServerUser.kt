package com.example.notfallapp.server

import android.content.Context
import com.android.volley.Request
import com.example.notfallapp.R
import com.example.notfallapp.bll.User
import com.example.notfallapp.menubar.settings.SettingsActivity
import com.example.notfallapp.server.ServerApi.Companion.createCall
import org.json.JSONException
import org.json.JSONObject
import java.lang.ClassCastException
import java.util.*

class ServerUser {

    fun getUserInfo(context: Context){
        createCall(Request.Method.GET, context.getString(R.string.UserMe), null ){ response ->
            if (response.has("ID")) {
                SettingsActivity.logInUser = User(
                    UUID.fromString(response.get("ID") as String?),
                    isStringOrNull("ForeignId", response),
                    isStringOrNull("Title", response),
                    response.getString("Forename"),
                    response.getString("Surname"),
                    response.getString("Username"),
                    response.getBoolean("Active"),
                    response.getString("Role"),
                    response.getInt("Gender"),
                    response.getBoolean("PhotoSet"),
                    isDateOrNull("BirthDay", response),
                    isStringOrNull("EmailAddress", response),
                    isStringOrNull("PhoneFixed", response),
                    response.getInt("OrgUnit"),
                    isStringOrNull("Language", response),
                    isStringOrNull("TimeZone", response)
                )
            }
        }
    }

    private fun isStringOrNull(key: String, response: JSONObject): String? {
        return if(response.getString(key) == null){
            null
        } else {
            try{
                response.getString(key)
            }catch (ex: JSONException){
                null
            }
        }
    }

    private fun isDateOrNull(key: String, response: JSONObject): Date?{
        return if(response.get(key) == null){
            null
        } else {
            try{
                response.get(key) as Date
            }catch (ex : ClassCastException) {
                null
            }
        }
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