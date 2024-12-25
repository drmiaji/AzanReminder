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
import java.text.SimpleDateFormat
import java.util.TimeZone

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
            .setContentTitle("التالي")
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

        // البحث عن الصلاة القادمة بناءً على الوقت الحالي
        for ((prayer, time) in prayerTimes) {
            if (currentTime.before(time)) {
                val timeRemaining = getTimeRemaining(currentTime, time)
                val prayerTime = formatPrayerTime(time)
                return "وقت صلاة $prayer: $prayerTime\nتبقى: $timeRemaining"
            }
        }

        return "لا توجد صلاة قادمة حالياً"
    }

    private fun formatPrayerTime(prayerTime: Calendar): String {
        // تعديل التوقيت إلى توقيت مصر (UTC + 2)
        val egyptTimeZone = TimeZone.getTimeZone("Africa/Cairo")
        prayerTime.timeZone = egyptTimeZone

        // استخدام تنسيق 12 ساعة مع AM/PM
        val timeFormat = SimpleDateFormat("hh:mm a", java.util.Locale("ar", "EG"))
        timeFormat.timeZone = egyptTimeZone
        return timeFormat.format(prayerTime.time)
    }

    private fun getTimeRemaining(currentTime: Calendar, prayerTime: Calendar): String {
        val timeDiffMillis = prayerTime.timeInMillis - currentTime.timeInMillis
        val hoursRemaining = (timeDiffMillis / (1000 * 60 * 60)-1).toInt()
        val minutesRemaining = ((timeDiffMillis / (1000 * 60)) % 60).toInt()

        // صياغة الوقت المتبقي
        return "%02d:%02d".format(hoursRemaining, minutesRemaining)
    }

    private fun getPrayerTimes(): Map<String, Calendar> {
        val prayerTimes = mutableMapOf<String, Calendar>()
        val calendar = Calendar.getInstance()

        // الفجر
        prayerTimes["الفجر"] = calendar.clone() as Calendar
        prayerTimes["الفجر"]!!.apply { set(Calendar.HOUR_OF_DAY, 5); set(Calendar.MINUTE, 0) }

        // الظهر
        prayerTimes["الظهر"] = calendar.clone() as Calendar
        prayerTimes["الظهر"]!!.apply { set(Calendar.HOUR_OF_DAY, 12); set(Calendar.MINUTE, 0) }

        // العصر
        prayerTimes["العصر"] = calendar.clone() as Calendar
        prayerTimes["العصر"]!!.apply { set(Calendar.HOUR_OF_DAY, 15); set(Calendar.MINUTE, 30) }

        // المغرب
        prayerTimes["المغرب"] = calendar.clone() as Calendar
        prayerTimes["المغرب"]!!.apply { set(Calendar.HOUR_OF_DAY, 18); set(Calendar.MINUTE, 0) }

        // العشاء
        prayerTimes["العشاء"] = calendar.clone() as Calendar
        prayerTimes["العشاء"]!!.apply { set(Calendar.HOUR_OF_DAY, 19); set(Calendar.MINUTE, 30) }

        return prayerTimes
    }

    companion object {
        fun schedulePrayerNotification(context: Context) {
            // تحديد التكرار الدوري هنا إذا كنت بحاجة لإرسال الإشعار بشكل دوري (مثل كل ساعة أو ساعتين)
            val workRequest = PeriodicWorkRequestBuilder<PrayerNotificationWorkerHome>(1, TimeUnit.HOURS).build()
            WorkManager.getInstance(context).enqueue(workRequest)
        }
    }
}
