package com.example.notfallapp.alarm

import android.content.Context
import android.content.Intent
import android.os.Handler
import android.provider.Settings
import android.util.Log
import androidx.core.content.ContextCompat.startActivity
import com.example.notfallapp.R
import com.example.notfallapp.bll.Alarm
import com.example.notfallapp.database.EmergencyAppDatabase
import com.example.notfallapp.interfaces.CurrentLocation
import com.example.notfallapp.interfaces.INotifications
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*


class TimerHandler {
    companion object : INotifications {
            const val timerAfterSosWillSend: Long = 5000
            private lateinit var handler: Handler
            fun timerHandler(context: Context){
                // this, when you would like to have the timer in the main thread
                //handler = Handler(Looper.getMainLooper())
                handler = Handler()
                handler.postDelayed({ //Do something after 10000ms
                    // here must the alarm send to the server
                    /*ServerAlarm().sendAlert()
                    ServerAlarm().sendPosition(context)*/

                    // meanwhile create alarm in DB
                    createAlarmInDb(context)

                    createNotificationSuccessfulAlarm(context)

                    val intent = Intent(context, AlarmSuccesfulActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(context, intent, null)
                }, timerAfterSosWillSend)
            }

            fun deleteTimer(){
                handler.removeCallbacksAndMessages(null)
            }

        private fun createAlarmInDb(context: Context){
            val androidId: String = Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
            val clickedTime: Date = Calendar.getInstance().time
            val dateFormat = android.text.format.DateFormat.format("dd-MM-yyyy kk:mm:ss", clickedTime)

            val location = CurrentLocation.currentLocation

            val alarm: Alarm?

            if (location != null) {
                alarm = try{
                    Alarm(androidId, location.longitude, location.latitude,
                        context.resources.getString(R.string.AlarmAccepted), dateFormat.toString())
                }catch (ex: java.lang.Exception){
                    Alarm(androidId, 0.0, 0.0,
                        context.resources.getString(R.string.AlarmAccepted), dateFormat.toString())
                }
            }else{
                alarm = try{
                    Alarm(androidId, 0.0, 0.0,
                        context.resources.getString(R.string.AlarmAccepted), dateFormat.toString())
                }catch (ex: java.lang.Exception){
                    Alarm(androidId, 0.0, 0.0,
                        context.resources.getString(R.string.AlarmAccepted), dateFormat.toString())
                }
            }

            val db = EmergencyAppDatabase.getInstance(context)

            try{
                GlobalScope.launch {
                    db.alarmsDao().insertAlarm(alarm)
                }
            }catch (ex: Exception){
                Log.e(context.resources.getString(R.string.ErrorHandler),
                      String.format(context.resources.getString(R.string.ErrorHandlerMessage),
                                    context.resources.getString(R.string.TimerHandler), ex.toString()))
            }
        }
    }
}