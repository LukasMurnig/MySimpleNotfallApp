package com.example.notfallapp.server

import android.content.Context
import android.widget.TextView
import com.android.volley.Request
import com.example.notfallapp.R
import com.example.notfallapp.bll.User
import com.example.notfallapp.menubar.settings.SettingsActivity
import org.json.JSONException
import org.json.JSONObject
import java.lang.ClassCastException
import java.util.*

class ServerUser {

    fun getUserInfo(context: Context, tvName: TextView, tvPhone: TextView, tvEmail: TextView){
        ServerApi.createJsonObjectRequest(Request.Method.GET, context.getString(R.string.UserMe), null ){ response ->
            if (response.has("ID")) {
                SettingsActivity.logInUser = User(
                    UUID.fromString(ResponseConverter().isStringOrNull("ID", response)),
                    ResponseConverter().isStringOrNull("ForeignId", response),
                    ResponseConverter().isStringOrNull("Title", response),
                    response.getString("Forename"),
                    response.getString("Surname"),
                    response.getString("Username"),
                    response.getBoolean("Active"),
                    response.getString("Role"),
                    response.getInt("Gender"),
                    response.getBoolean("PhotoSet"),
                    ResponseConverter().isDateOrNull("BirthDay", response),
                    ResponseConverter().isStringOrNull("EmailAddress", response),
                    ResponseConverter().isStringOrNull("PhoneFixed", response),
                    response.getInt("OrgUnit"),
                    ResponseConverter().isStringOrNull("Language", response),
                    ResponseConverter().isStringOrNull("TimeZone", response)
                )
                tvName.text = SettingsActivity.logInUser!!.forename + " " + SettingsActivity.logInUser!!.surname
                tvPhone.text = SettingsActivity.logInUser!!.phoneFixed
                tvEmail.text = SettingsActivity.logInUser!!.emailAddress
            }
        }
    }
}