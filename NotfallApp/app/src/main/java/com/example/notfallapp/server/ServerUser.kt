package com.example.notfallapp.server

import android.content.Context
import com.android.volley.Request
import com.example.notfallapp.R
import com.example.notfallapp.bll.User
import com.example.notfallapp.menubar.settings.SettingsActivity
import com.example.notfallapp.server.ServerApi.Companion.createCall
import org.json.JSONException
import org.json.JSONObject
import java.util.*

class ServerUser {

    fun getUserInfo(context: Context){
        createCall(Request.Method.GET, context.getString(R.string.UserMe), null ){ response ->
            if (response.has("data")) {
                val data = response.getJSONObject("data")
                SettingsActivity.logInUser = User(
                    data.get("ID") as UUID,
                    proveIfNullOrValue("ForeignId", data) as String?,
                    proveIfNullOrValue("Title", data) as String?,
                    data.getString("Forename"),
                    data.getString("Surename"),
                    data.getString("Username"),
                    data.getBoolean("Active"),
                    data.getString("Role"),
                    data.getBoolean("Gender"),
                    data.getBoolean("PhotoSet"),
                    proveIfNullOrValue("BirthDay", data) as Date?,
                    proveIfNullOrValue("EmailAddress", data) as String?,
                    proveIfNullOrValue("PhoneFixed", data) as String?,
                    data.getInt("OrgUnit"),
                    proveIfNullOrValue("Language", data) as String?,
                    proveIfNullOrValue("Timezone", data) as String?
                )
            }
        }
        // solange Server noch nicht funktioniert
        val json = JSONObject()
            json.put("ID", UUID(1234,123))
            json.put("ForeignId", null)
            json.put("Title", "Dr")
            json.put("Forename", "Maria")
            json.put("Surename", "Musterfrau")
            json.put("Username", "mamufrau")
            json.put("Active", true)
            json.put("Role", "User")
            json.put("Gender", false)
            json.put("PhotoSet", false)
            json.put("BirthDay", Date())
            json.put("EmailAddress", "maria.musterfrau@mail.com")
            json.put("PhoneFixed", "06769392808")
            json.put("OrgUnit", 3)
            json.put("Language", null)
            json.put("Timezone", null)

            SettingsActivity.logInUser = User(
                json.get("ID") as UUID,
                proveIfNullOrValue("ForeignId", json) as String?,
                proveIfNullOrValue("Title", json) as String?,
                json.getString("Forename"),
                json.getString("Surename"),
                json.getString("Username"),
                json.getBoolean("Active"),
                json.getString("Role"),
                json.getBoolean("Gender"),
                json.getBoolean("PhotoSet"),
                proveIfNullOrValue("BirthDay", json) as Date?,
                proveIfNullOrValue("EmailAddress", json) as String?,
                proveIfNullOrValue("PhoneFixed", json) as String?,
                json.getInt("OrgUnit"),
                proveIfNullOrValue("Language", json) as String?,
                proveIfNullOrValue("Timezone", json) as String?
            )
    }

    private fun proveIfNullOrValue(key: String, response: JSONObject): Any?{
        return try{
            response.get(key)
        }catch (ex: JSONException){
            null
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