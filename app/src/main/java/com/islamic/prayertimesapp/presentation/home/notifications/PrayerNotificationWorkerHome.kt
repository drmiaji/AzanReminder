package com.islamic.prayertimesapp.presentation.home.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.core.app.NotificationCompat
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.islamic.prayertimesapp.R
import java.util.Calendar
import java.util.SimpleTimeZone
import java.util.TimeZone
import java.text.SimpleDateFormat
import java.util.concurrent.TimeUnit

class PrayerNotificationWorkerHome(context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {

    override fun doWork(): Result {
        // عرض الإشعار بالعد التنازلي للصلاة القادمة
        showPrayerNotification()
        return Result.success()
    }

    private fun showPrayerNotification() {
        val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // إنشاء قناة إشعار إذا لزم الأمر
        val channelId = "prayer_notification_channel"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (notificationManager.getNotificationChannel(channelId) == null) {
                val channel = NotificationChannel(
                    channelId,
                    "Prayer Notifications",
                    NotificationManager.IMPORTANCE_HIGH
                ).apply {
                    description = "Channel for prayer notifications"
                }
                notificationManager.createNotificationChannel(channel)
            }
        }

        // تخصيص النص للإشعار بناءً على الصلاة القادمة
        val nextPrayerDetails = getNextPrayer()
        val notification = NotificationCompat.Builder(applicationContext, channelId)
            .setContentTitle("الصلاة القادمة")
            .setContentText(nextPrayerDetails)
            .setSmallIcon(R.drawable.logoazan)
            .setAutoCancel(true)
            // تحديد نغمة الصوت من ملف .ogg
            .setSound(Uri.parse("android.resource://${applicationContext.packageName}/${R.raw.azan}"))
            .setVibrate(longArrayOf(0, 500, 500, 500)) // اهتزاز بنمط معين
            .build()

        // عرض الإشعار بمعرف ثابت لتجنب تراكم الإشعارات
        val notificationId = 1 // معرف ثابت للإشعار
        notificationManager.notify(notificationId, notification)
    }

    private fun getNextPrayer(): String {
        val currentTime = Calendar.getInstance()
        currentTime.apply {
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val prayerTimes = getPrayerTimes(currentTime)

        // تحقق من أوقات الصلاة في اليوم الحالي
        for ((prayer, time) in prayerTimes) {
            if (currentTime.before(time)) {
                val timeRemaining = getTimeRemaining(currentTime, time)
                val prayerTime = formatPrayerTime(time)
                return "صلاة $prayer: $prayerTime\nالوقت المتبقي: $timeRemaining"
            }
        }

        // إذا انتهت جميع الصلوات، الانتقال إلى صلوات اليوم التالي
        val nextDay = Calendar.getInstance().apply {
            add(Calendar.DAY_OF_MONTH, 1)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val nextDayPrayerTimes = getPrayerTimes(nextDay)
        val firstPrayer = nextDayPrayerTimes.entries.first()
        val timeRemaining = getTimeRemaining(currentTime, firstPrayer.value)
        val prayerTime = formatPrayerTime(firstPrayer.value)

        return "صلاة ${firstPrayer.key} التالي: $prayerTime\nالوقت المتبقي: $timeRemaining"
    }

    private fun formatPrayerTime(prayerTime: Calendar): String {
        val egyptTimeZone = TimeZone.getTimeZone("Africa/Cairo")
        prayerTime.timeZone = egyptTimeZone
        val timeFormat = SimpleDateFormat("hh:mm a", java.util.Locale("ar", "EG"))
        timeFormat.timeZone = egyptTimeZone
        return timeFormat.format(prayerTime.time)
    }

    private fun getTimeRemaining(currentTime: Calendar, prayerTime: Calendar): String {
        val timeDiffMillis = prayerTime.timeInMillis - currentTime.timeInMillis
        val hoursRemaining = (timeDiffMillis / (1000 * 60 * 60)).toInt()
        val minutesRemaining = ((timeDiffMillis / (1000 * 60)) % 60).toInt()
        val secondsRemaining = ((timeDiffMillis / 1000) % 60).toInt()
        return "%02d:%02d:%02d".format(hoursRemaining, minutesRemaining, secondsRemaining)
    }

    private fun getPrayerTimes(baseTime: Calendar): Map<String, Calendar> {
        val egyptTimeZone = TimeZone.getTimeZone("Africa/Cairo")
        val prayerTimes = mutableMapOf<String, Calendar>()
        val calendar = baseTime.clone() as Calendar
        calendar.timeZone = egyptTimeZone

        prayerTimes["الفجر"] = calendar.clone() as Calendar
        prayerTimes["الفجر"]!!.apply { set(Calendar.HOUR_OF_DAY, 5); set(Calendar.MINUTE, 25) }

        prayerTimes["الظهر"] = calendar.clone() as Calendar
        prayerTimes["الظهر"]!!.apply { set(Calendar.HOUR_OF_DAY, 11); set(Calendar.MINUTE, 56) }

        prayerTimes["العصر"] = calendar.clone() as Calendar
        prayerTimes["العصر"]!!.apply { set(Calendar.HOUR_OF_DAY, 14); set(Calendar.MINUTE, 43) }

        prayerTimes["المغرب"] = calendar.clone() as Calendar
        prayerTimes["المغرب"]!!.apply { set(Calendar.HOUR_OF_DAY, 17); set(Calendar.MINUTE, 1) }

        prayerTimes["العشاء"] = calendar.clone() as Calendar
        prayerTimes["العشاء"]!!.apply { set(Calendar.HOUR_OF_DAY, 18); set(Calendar.MINUTE, 27) }

        return prayerTimes
    }

    companion object {
        fun schedulePrayerNotification(context: Context) {
            val workRequest = PeriodicWorkRequestBuilder<PrayerNotificationWorkerHome>(
                15, TimeUnit.MINUTES
            ).build()

            // جدولة العمل الدوري بمعرف فريد لتجنب التكرار
            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                "PrayerNotificationWorkerHome",
                androidx.work.ExistingPeriodicWorkPolicy.REPLACE,
                workRequest
            )
        }
    }
}
