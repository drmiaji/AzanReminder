package com.islamic.prayertimesapp.presentation.splash.notifications

import androidx.work.WorkManager
import androidx.work.OneTimeWorkRequestBuilder
import android.content.Context

fun schedulePrayerNotification(context: Context) {
    val workRequest = OneTimeWorkRequestBuilder<PrayerNotificationWorker>()
        .build()

    WorkManager.getInstance(context).enqueue(workRequest)
}
