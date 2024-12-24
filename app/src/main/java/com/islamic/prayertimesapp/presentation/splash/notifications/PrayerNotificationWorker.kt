package com.islamic.prayertimesapp.presentation.splash.notifications

import android.content.Context
import android.app.NotificationManager
import android.app.NotificationChannel
import android.net.Uri
import androidx.core.app.NotificationCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import android.os.Build
import com.islamic.prayertimesapp.R
import java.util.concurrent.TimeUnit

class PrayerNotificationWorker(context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {

    override fun doWork(): Result {
        // هنا يمكنك إضافة الكود الخاص بإرسال إشعار الصلاة على النبي
        showPrayerNotification()
        return Result.success() // يمكنك إرجاع نتيجة سواء كانت نجاح أو فشل
    }

    private fun showPrayerNotification() {
        val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // التحقق من إصدار Android لإنشاء قناة الإشعار
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel("prayer_notification_channel", "Prayer Notifications", NotificationManager.IMPORTANCE_DEFAULT)
            notificationManager.createNotificationChannel(channel)
            NotificationManager.IMPORTANCE_HIGH
        }

        // تعيين الصوت المخصص للإشعار
        val soundUri: Uri = Uri.parse("android.resource://${applicationContext.packageName}/raw/file.mp3") // تأكد من أن اسم الملف صحيح

        val notification = NotificationCompat.Builder(applicationContext, "prayer_notification_channel")
            .setContentTitle("الصلاة على النبي")
            .setContentText("اللهم صل وسلم على نبينا محمد")
            .setSmallIcon(R.drawable.logoazan)
            .setAutoCancel(true)
            .setSound(soundUri) // تعيين الصوت هنا
            .build()

        notificationManager.notify(1, notification)
    }

    companion object {
        // دالة لجدولة إشعار دوري كل ساعة
        fun schedulePrayerNotification(context: Context) {
            val workRequest = PeriodicWorkRequestBuilder<PrayerNotificationWorker>(1, TimeUnit.HOURS) // كل ساعة
                .build()

            WorkManager.getInstance(context).enqueue(workRequest)
        }
    }
}
