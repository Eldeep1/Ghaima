package com.depogramming.ghaima.worker.reciever

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.RingtoneManager
import androidx.core.app.NotificationCompat
import com.depogramming.ghaima.R
import com.depogramming.ghaima.data.alerts.datasource.local.AlertsLocalDataSource
import com.depogramming.ghaima.data.alerts.model.ExactAlarmAlert
import com.depogramming.ghaima.data.alerts.repository.AlertsRepositoryImpl
import com.depogramming.ghaima.data.db.AppDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ExactAlarmReceiver : BroadcastReceiver() {


    override fun onReceive(context: Context, intent: Intent) {
        val pendingResult = goAsync()
        val alarmId = intent.getIntExtra("ALARM_ID", -1)
        val alertsRepo = AlertsRepositoryImpl(AlertsLocalDataSource(AppDatabase.getInstance(context).alertsDao()))

        CoroutineScope(Dispatchers.IO).launch {
            try {
                if (alarmId != -1) {
                    val activeAlert = alertsRepo.getAlertById(alarmId)

                    if (activeAlert != null && activeAlert is ExactAlarmAlert) {

                        showSystemAlarm(context,
                            context.getString(R.string.weather_alarm), activeAlert.description)

                        val turnedOffAlert = activeAlert.copy(isActive = false)
                        alertsRepo.insertAlert(turnedOffAlert)

                    }
                }
            } finally {
                pendingResult.finish()
            }
        }
    }
}

private fun showSystemAlarm(context: Context, title: String, message: String) {
    val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    val channelId = "weather_alerts_channel_v3"

    val alarmSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)

    val channel = NotificationChannel(
        channelId,
        "Weather Alerts",
        NotificationManager.IMPORTANCE_HIGH
    ).apply {
        description = "Alerts for user-defined weather conditions"

        val audioAttributes = AudioAttributes.Builder()
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .setUsage(AudioAttributes.USAGE_ALARM)
            .build()

        setSound(alarmSoundUri, audioAttributes)
    }
    notificationManager.createNotificationChannel(channel)


    val notificationId = System.currentTimeMillis().toInt()

    val dismissIntent = Intent(context, DismissAlarmReceiver::class.java).apply {
        putExtra("NOTIFICATION_ID", notificationId)
    }

    val dismissPendingIntent = PendingIntent.getBroadcast(
        context,
        notificationId,
        dismissIntent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    val notification = NotificationCompat.Builder(context, channelId)
        .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
        .setContentTitle(title)
        .setContentText(message)
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .setCategory(NotificationCompat.CATEGORY_ALARM)
        .addAction(
            android.R.drawable.ic_menu_close_clear_cancel,
            context.getString(R.string.dismiss),
            dismissPendingIntent
        )
        .setOngoing(true)
        .setAutoCancel(false)
        .build()

    notification.flags = notification.flags or NotificationCompat.FLAG_INSISTENT

    notificationManager.notify(notificationId, notification)
}