package com.islamic.prayertimesapp.presentation.home.notifications

import android.content.Context
import android.app.NotificationManager
import android.app.NotificationChannel
import android.app.PendingIntent
import android.content.Intent
import android.net.Uri
import androidx.core.app.NotificationCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import android.os.Build
import com.islamic.prayertimesapp.R
import com.islamic.prayertimesapp.presentation.MainActivity
import java.util.concurrent.TimeUnit
import java.util.Calendar

class PrayerNotificationWorkerHome(context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {

    override fun doWork(): Result {
        // إرسال إشعار بأوقات الصلاة الفائتة
        showPrayerNotification()
        return Result.success() // يمكنك إرجاع نتيجة سواء كانت نجاح أو فشل
    }

    private fun showPrayerNotification() {
        val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // التحقق من إصدار Android لإنشاء قناة الإشعار
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel("prayer_notification_channel", "Prayer Notifications", NotificationManager.IMPORTANCE_DEFAULT)
            notificationManager.createNotificationChannel(channel)
        }

        // تعيين الصوت المخصص للإشعار
        val soundUri: Uri = Uri.parse("android.resource://${applicationContext.packageName}/raw/file") // تأكد من أن اسم الملف صحيح

        // تخصيص نص الإشعار
        val missedPrayersText = getMissedPrayers() // هنا ستحصل على الأوقات الفائتة
        val notification = NotificationCompat.Builder(applicationContext, "prayer_notification_channel")
            .setContentTitle("الأوقات الفائتة للصلاة")
            .setContentText(missedPrayersText) // إضافة النص المتعلق بالصلاة الفائتة
            .setSmallIcon(R.drawable.logoazan)
            .setAutoCancel(true)
            .setSound(soundUri) // تعيين الصوت هنا
            .build()

        notificationManager.notify(1, notification)
    }

    // دالة لحساب الأوقات الفائتة
    private fun getMissedPrayers(): String {
        val currentTime = Calendar.getInstance()
        val prayerTimes = getPrayerTimes() // دالة للحصول على أوقات الصلاة من التطبيق أو API
        val missedPrayers = StringBuilder()

        // التحقق من الأوقات الفائتة ومقارنة الوقت الحالي مع أوقات الصلاة
        if (currentTime.after(prayerTimes["fajr"]!!)) missedPrayers.append("فاتتك صلاة الفجر\n")
        if (currentTime.after(prayerTimes["dhuhr"]!!)) missedPrayers.append("فاتتك صلاة الظهر\n")
        if (currentTime.after(prayerTimes["asr"]!!)) missedPrayers.append("فاتتك صلاة العصر\n")
        if (currentTime.after(prayerTimes["maghrib"]!!)) missedPrayers.append("فاتتك صلاة المغرب\n")
        if (currentTime.after(prayerTimes["isha"]!!)) missedPrayers.append("فاتتك صلاة العشاء\n")

        return if (missedPrayers.isNotEmpty()) missedPrayers.toString()
        else "لم تفوتك أي صلوات"

        val intent = Intent(applicationContext, MainActivity::class.java).apply {
            // يمكنك إضافة بيانات إضافية إذا لزم الأمر
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        // إعداد PendingIntent لفتح Activity عند الضغط على الإشعار
        val pendingIntent = PendingIntent.getActivity(applicationContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)

    }

    // دالة للحصول على أوقات الصلاة (قد تأتي من API أو قاعدة بيانات)
    private fun getPrayerTimes(): Map<String, Calendar> {
        // مثال على أوقات الصلاة، يمكن استبدالها بالقيم الحقيقية
        val prayerTimes = mutableMapOf<String, Calendar>()
        val calendar = Calendar.getInstance()

        // تحديد أوقات الصلاة (يجب تحديد الأوقات الفعلية هنا)
        prayerTimes["fajr"] = calendar.apply { set(Calendar.HOUR_OF_DAY, 5); set(Calendar.MINUTE, 0) }
        prayerTimes["dhuhr"] = calendar.apply { set(Calendar.HOUR_OF_DAY, 12); set(Calendar.MINUTE, 0) }
        prayerTimes["asr"] = calendar.apply { set(Calendar.HOUR_OF_DAY, 15); set(Calendar.MINUTE, 30) }
        prayerTimes["maghrib"] = calendar.apply { set(Calendar.HOUR_OF_DAY, 18); set(Calendar.MINUTE, 0) }
        prayerTimes["isha"] = calendar.apply { set(Calendar.HOUR_OF_DAY, 19); set(Calendar.MINUTE, 30) }

        return prayerTimes
    }

    companion object {
        // دالة لجدولة إشعار دوري كل ساعة
        fun schedulePrayerNotification(context: Context) {
            val workRequest = PeriodicWorkRequestBuilder<PrayerNotificationWorkerHome>(1, TimeUnit.HOURS) // كل ساعة
                .build()

            WorkManager.getInstance(context).enqueue(workRequest)
        }
    }
}
