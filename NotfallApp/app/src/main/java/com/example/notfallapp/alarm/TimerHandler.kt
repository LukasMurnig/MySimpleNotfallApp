package com.example.notfallapp.alarm

import android.Manifest
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Handler
import android.provider.Settings
import android.widget.RemoteViews
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat.startActivity
import androidx.room.Room
import com.example.notfallapp.R
import com.example.notfallapp.bll.Alarm
import com.example.notfallapp.database.AlarmDatabase
import com.example.notfallapp.interfaces.INotifications
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*


class TimerHandler {
    companion object : INotifications {
            private lateinit var handler: Handler

            fun timerHandler(context: Context){
                // this, when you would like to have the timer in the main thread
                //handler = Handler(Looper.getMainLooper())
                handler = Handler()
                handler.postDelayed({ //Do something after 10000ms
                    // here must the alarm send to the server


                    // create alarm in DB
                    createAlarmInDb(context)

                    createNotificationSuccessfulAlarm(context)

                    val intent = Intent(context, AlarmSuccesfulActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(context, intent, null)
                }, 5000)
            }

            fun deleteTimer(){
                handler.removeCallbacksAndMessages(null)
            }

        private fun createAlarmInDb(context: Context){
            val androidId: String = Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
            val clickedTime: Date = Calendar.getInstance().time
            val dateFormat = android.text.format.DateFormat.format("dd-MM-yyyy hh:mm:ss a", clickedTime)

            val lm = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // TODO get permission for GPS
                return
            }
            val location =  lm.getLastKnownLocation(LocationManager.GPS_PROVIDER)

            val alarm: Alarm?

            alarm = try{
                Alarm(androidId, location.longitude, location.latitude, "nobody now", dateFormat.toString())
            }catch (ex: java.lang.Exception){
                Alarm(androidId, 0.0, 0.0, "nobody now", dateFormat.toString())
            }

            val db = Room.databaseBuilder(context, AlarmDatabase::class.java, "alarms.db").fallbackToDestructiveMigration().build()
            try{
                GlobalScope.launch {
                    // zum Testen
                    //db.alarmsDao().deleteAll()

                    db.alarmsDao().insertAlarm(alarm)
                }
            }catch (ex: Exception){
                println("Konnte Alarm nicht speichern. Grund: $ex")
            }
        }
    }
}