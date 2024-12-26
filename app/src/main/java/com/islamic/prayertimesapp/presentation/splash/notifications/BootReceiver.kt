package com.islamic.prayertimesapp.presentation.splash.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.work.WorkManager
import com.islamic.prayertimesapp.presentation.splash.notifications.PrayerNotificationWorker

class BootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action.equals(Intent.ACTION_BOOT_COMPLETED) || intent.action.equals(Intent.ACTION_MY_PACKAGE_REPLACED)) {
            // إذا تم الإقلاع بنجاح، نقوم بجدولة الإشعار
            PrayerNotificationWorker.schedulePrayerNotification(context)
        }
    }
}
