package com.example.notfallapp.server

import org.json.JSONException
import org.json.JSONObject
import java.lang.ClassCastException
import java.util.*

/**
 * Convert some server response fields to their right format
 */
class ResponseConverter {

    /**
     * function which try to convert a string to UUID or null
     */
    fun convertFromStringToUUID(string: String?): UUID? {
        return if(string == null || string.isEmpty() || string == "null"){
            null
        } else {
            UUID.fromString(string)
        }
    }

    /**
     * function which return null or a Double value
     */
    fun isDoubleOrNull(key: String, response: JSONObject): Double? {
        return try{
            if(response.get(key) == null || response.get(key) == "null"){
                null
            } else run {
                response.getDouble(key)
            }
        }catch (ex: JSONException){
            null
        }
    }

    /**
     * function which return null or a Date value
     */
    fun isDateOrNull(key: String, response: JSONObject): Date?{
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

    /**
     * function which return null or a String value
     */
    fun isStringOrNull(key: String, response: JSONObject): String? {
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

}