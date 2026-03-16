package com.depogramming.ghaima.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.depogramming.ghaima.data.alerts.model.NotificationAlert
import com.depogramming.ghaima.data.alerts.repository.AlertsRepo
import com.depogramming.ghaima.data.weather.WeatherRepositoryImpl
import java.util.Calendar
import android.app.NotificationChannel
import android.app.NotificationManager
import androidx.core.app.NotificationCompat
import com.depogramming.ghaima.data.alerts.datasource.local.AlertsLocalDataSource
import com.depogramming.ghaima.data.alerts.repository.AlertsRepositoryImpl
import com.depogramming.ghaima.data.db.AppDatabase
import android.app.PendingIntent
import android.content.Intent
import com.depogramming.ghaima.MainActivity
import com.depogramming.ghaima.R
import com.depogramming.ghaima.data.network.Network
import com.depogramming.ghaima.data.weather.datasource.local.WeatherLocalDataSource
import com.depogramming.ghaima.data.weather.datasource.local.entity.WeatherForecastEntity
import com.depogramming.ghaima.data.weather.datasource.remote.WeatherRemoteDataSource
import com.depogramming.ghaima.data.weather.mapper.toEntity
import kotlin.jvm.java
import kotlin.math.abs

class WeatherAlertWorker(
    private val context: Context,
    workerParams: WorkerParameters,
) : CoroutineWorker(context, workerParams) {

    val alertsRepository: AlertsRepo =
        AlertsRepositoryImpl(AlertsLocalDataSource(AppDatabase.getInstance(context).alertsDao()))

    val weatherRepo = WeatherRepositoryImpl(
        weatherRemoteDataSource = WeatherRemoteDataSource(Network.weatherService),
        weatherLocalDataSource = WeatherLocalDataSource(AppDatabase.getInstance(context).weatherForecastDao(),
            AppDatabase.getInstance(context).favouritesDao()),
        )

    override suspend fun doWork(): Result {
        return try {

            val activeAlerts = alertsRepository.getActiveAlertsSync().getOrNull() ?: emptyList()

            val activeNotifications =
                activeAlerts.filterIsInstance<NotificationAlert>().filter { it.isActive }

            if (activeNotifications.isEmpty()) {
                return Result.success()
            }

            val calendar = Calendar.getInstance()
            val currentMinutes =
                (calendar.get(Calendar.HOUR_OF_DAY) * 60) + calendar.get(Calendar.MINUTE)

            val currentWeather = weatherRepo.getLocalWeatherForecast()

            for (alert in activeNotifications) {
                val isWithinTimeWindow = if (alert.windowStart <= alert.windowEnd) {
                    currentMinutes in alert.windowStart..alert.windowEnd
                } else {
                    currentMinutes >= alert.windowStart || currentMinutes <= alert.windowEnd
                }

                if (isWithinTimeWindow) {
                    val temp = getCurrentHourlyTemperature(currentWeather.toEntity())?:20.0
                    if (temp >= alert.minTemp && temp <= alert.maxTemp) {

                        showSystemNotification(
                            context = context,
                            title = context.getString(R.string.weather_notification),
                            message = alert.description.ifBlank {
                                context.getString(
                                    R.string.temperature_is_currently_c,
                                    temp.toInt()
                                ) }
                        )

                        alertsRepository.insertAlert(alert.copy(
                            isActive = false
                        ))
                    }
                }
            }

            Result.success()
        } catch (e: Exception) {
            e.printStackTrace()
            Result.retry()
        }
    }

    private fun showSystemNotification(context: Context, title: String, message: String) {
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager


        val channelId = "weather_alerts_channel_normal"

            val channel = NotificationChannel(
                channelId,
                "Weather Alerts",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Alerts for user-defined weather conditions"
            }
            notificationManager.createNotificationChannel(channel)



        val tapIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val tapPendingIntent = PendingIntent.getActivity(
            context,
            0,
            tapIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notificationId = System.currentTimeMillis().toInt()


        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(tapPendingIntent)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(notificationId, notification)
    }


    private fun getCurrentHourlyTemperature(cachedWeather: WeatherForecastEntity): Double? {
        val currentHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)

        val closestHourlyModel = cachedWeather.hourlyForecast.take(7).minByOrNull { hourlyModel ->

            val forecastHour = extractHourFromString(hourlyModel.time)
                ?: return@minByOrNull Int.MAX_VALUE

            abs(currentHour - forecastHour)
        }

        val rawTempString = closestHourlyModel?.temperature ?: cachedWeather.current.temperature
        val cleanTempString = rawTempString.replace(Regex("[^0-9.-]"), "")
        return cleanTempString.toDoubleOrNull()
    }


    private fun extractHourFromString(timeString: String): Int? {
        val match = Regex("(\\d{1,2}):").find(timeString)
        val hour = match?.groupValues?.get(1)?.toIntOrNull()
        return hour
    }

}