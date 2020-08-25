package com.example.notfallapp.server

import android.content.Context
import android.widget.TextView
import com.android.volley.Request
import com.example.notfallapp.R
import com.example.notfallapp.bll.User
import com.example.notfallapp.menubar.settings.SettingsActivity
import java.util.*

/**
 * class to get the data of the logged in User from the Server
 */
class ServerUser {

    /**
     * create the request, handle the response and fill data to three text views
     */
    fun getUserInfo(context: Context, tvName: TextView?, tvPhone: TextView?, tvEmail: TextView?){
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
                if(tvName != null && tvPhone != null && tvEmail != null){
                    tvName.text = SettingsActivity.logInUser!!.forename + " " + SettingsActivity.logInUser!!.surname
                    tvPhone.text = SettingsActivity.logInUser!!.phoneFixed
                    tvEmail.text = SettingsActivity.logInUser!!.emailAddress
                }
            }
        }
    }

    fun getUserName(userId: String, tvUser: TextView){
        if(userId == SettingsActivity.logInUser?.id.toString()){
            tvUser.text = (SettingsActivity.logInUser?.surname) + ", " + (SettingsActivity.logInUser?.forename)
            return
        }

        ServerAlarm().createGetArrayCall(Request.Method.GET, "/users/mycontacts/"){ response ->
            if(response.length() == 0){
                return@createGetArrayCall
            }else{
                var name: String? = null

                for (i in 0 until response.length()) {
                    val js = response.getJSONObject(i)

                    if((js.get("ID") as String) == userId){
                        name = js.getString("Surname") + ", " + js.getString("Forename")
                    }

                }

                if(name != null){
                    tvUser.text = name
                } else {
                    tvUser.text = "Server"
                }
            }
        }
    }
}