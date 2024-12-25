package com.islamic.prayertimesapp.presentation.countdownWidget

import android.app.PendingIntent

import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.SystemClock
import android.preference.PreferenceManager
import android.widget.RemoteViews
import androidx.fragment.app.activityViewModels
import com.islamic.prayertimesapp.R
import com.islamic.prayertimesapp.common.util.Constants.Companion.COUNTDOWN_TIME_KEY
import dagger.hilt.android.AndroidEntryPoint
import java.util.concurrent.TimeUnit
import android.app.AlarmManager
import android.appwidget.AppWidgetManager

@AndroidEntryPoint
class countdownWidget : AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        // Set the alarm to update widget periodically
        setUpdateAlarm(context)

        // Update all the widgets
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onEnabled(context: Context) {
        // Enter relevant functionality for when the first widget is created
    }

    override fun onDisabled(context: Context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    private fun setUpdateAlarm(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, countdownWidget::class.java)
        intent.action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
        val pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)

        // Set the alarm to update the widget every minute
        alarmManager.setRepeating(
            AlarmManager.RTC,
            System.currentTimeMillis(),
            AlarmManager.INTERVAL_FIFTEEN_MINUTES,
            pendingIntent
        )
    }
}

internal fun updateAppWidget(
    context: Context,
    appWidgetManager: AppWidgetManager,
    appWidgetId: Int
) {
    val preferences: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
    val timeMillis = preferences.getLong(COUNTDOWN_TIME_KEY, 0L)

    // Construct the RemoteViews object
    val views = RemoteViews(context.packageName, R.layout.countdown_widget)

    // Calculate the remaining time in milliseconds
    val currentTimeMillis = System.currentTimeMillis()
    val remainingTimeMillis = timeMillis - currentTimeMillis

    // Convert remaining time to hours, minutes, seconds
    val hours = TimeUnit.MILLISECONDS.toHours(remainingTimeMillis)
    val minutes = TimeUnit.MILLISECONDS.toMinutes(remainingTimeMillis) - TimeUnit.HOURS.toMinutes(hours)
    val seconds = TimeUnit.MILLISECONDS.toSeconds(remainingTimeMillis) - TimeUnit.MINUTES.toSeconds(minutes)

    // Format the time as a string
    val timeString = String.format("%02d hr %02d min %02d sec", hours, minutes, seconds)

    // Set the time string to a TextView
    views.setTextViewText(R.id.countDown, timeString)

    // Instruct the widget manager to update the widget
    appWidgetManager.updateAppWidget(appWidgetId, views)
}
