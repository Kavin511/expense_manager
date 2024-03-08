package com.devstudio.data.repository

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import java.util.Calendar
import javax.inject.Inject

interface RemainderRepositoryInterface {
    fun setRemainders(remainder: List<Remainder>, context: Context)
    fun getRemainders()
}

class RemainderRepository @Inject constructor() :
    RemainderRepositoryInterface {
    override fun setRemainders(remainder: List<Remainder>, context: Context) {
        remainder.forEach {
            val intent = Intent(context, RemainderReceiver::class.java)
            intent.putExtra("value", it.toString())
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                1000 + it.dayOfWeek,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
            )
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val cal = Calendar.getInstance()
            cal.set(Calendar.HOUR_OF_DAY, it.hours)
            cal.set(Calendar.MINUTE, it.minutes)
            cal.set(Calendar.DAY_OF_WEEK, it.dayOfWeek)
            val timeMs = cal.timeInMillis
            alarmManager.setRepeating(
                AlarmManager.RTC_WAKEUP,
                timeMs,
                AlarmManager.INTERVAL_DAY,
                pendingIntent,
            )
        }
    }

    override fun getRemainders() {
    }
}

data class Remainder(
    val dayOfWeek: Int,
    val hours: Int = 12,
    val minutes: Int = 0,
    val isEnabled: Boolean,
) {
    override fun toString(): String {
        return "$dayOfWeek-$hours-$minutes"
    }
}

class RemainderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        Log.d("TAG", "onReceive: ${intent?.getStringExtra("value")}")
    }
}
