package com.islamic.prayertimesapp.presentation.home.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.net.Uri
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.islamic.prayertimesapp.R
import java.util.concurrent.TimeUnit
import java.util.Calendar

class PrayerNotificationWorkerHome(context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {

    override fun doWork(): Result {
        // إرسال إشعار بالصلاة القادمة بناءً على الوقت الحالي
        showPrayerNotification()
        return Result.success()
    }

    private fun showPrayerNotification() {
        val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // التحقق من إصدار Android لإنشاء قناة الإشعار مرة واحدة فقط
        val channelId = "prayer_notification_channel"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (notificationManager.getNotificationChannel(channelId) == null) {
                val channel = NotificationChannel(
                    channelId,
                    "Prayer Notifications",
                    NotificationManager.IMPORTANCE_DEFAULT
                )
                notificationManager.createNotificationChannel(channel)
            }
        }

        // تعيين الصوت المخصص للإشعار
        val soundUri: Uri = Uri.parse("android.resource://${applicationContext.packageName}/raw/azan") // تأكد من اسم الملف

        // تخصيص نص الإشعار للصلاة القادمة
        val nextPrayerText = getNextPrayer()
        val notification = NotificationCompat.Builder(applicationContext, channelId)
            .setContentTitle("حان وقت الصلاة")
            .setContentText(nextPrayerText)
            .setSmallIcon(R.drawable.logoazan)
            .setAutoCancel(true)
            .setSound(soundUri)
            .build()

        notificationManager.notify(1, notification)
    }

    private fun getNextPrayer(): String {
        val currentTime = Calendar.getInstance()
        currentTime.apply {
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val prayerTimes = getPrayerTimes()

        for ((prayer, time) in prayerTimes) {
            if (currentTime.before(time)) {
                return "حان وقت صلاة $prayer"
            }
        }

        return "لا توجد صلاة قادمة حالياً"
    }

    private fun getPrayerTimes(): Map<String, Calendar> {
        val prayerTimes = mutableMapOf<String, Calendar>()
        val calendar = Calendar.getInstance()

        prayerTimes["الفجر"] = calendar.clone() as Calendar
        prayerTimes["الفجر"]!!.apply { set(Calendar.HOUR_OF_DAY, 5); set(Calendar.MINUTE, 0) }

        prayerTimes["الظهر"] = calendar.clone() as Calendar
        prayerTimes["الظهر"]!!.apply { set(Calendar.HOUR_OF_DAY, 12); set(Calendar.MINUTE, 0) }

        prayerTimes["العصر"] = calendar.clone() as Calendar
        prayerTimes["العصر"]!!.apply { set(Calendar.HOUR_OF_DAY, 15); set(Calendar.MINUTE, 30) }

        prayerTimes["المغرب"] = calendar.clone() as Calendar
        prayerTimes["المغرب"]!!.apply { set(Calendar.HOUR_OF_DAY, 18); set(Calendar.MINUTE, 0) }

        prayerTimes["العشاء"] = calendar.clone() as Calendar
        prayerTimes["العشاء"]!!.apply { set(Calendar.HOUR_OF_DAY, 19); set(Calendar.MINUTE, 30) }

        return prayerTimes
    }

    companion object {
        fun schedulePrayerNotification(context: Context) {
            val workRequest = PeriodicWorkRequestBuilder<PrayerNotificationWorkerHome>(1, TimeUnit.HOURS).build()
            WorkManager.getInstance(context).enqueue(workRequest)
        }
    }
}
