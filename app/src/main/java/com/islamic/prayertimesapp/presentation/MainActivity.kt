package com.islamic.prayertimesapp.presentation

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.islamic.prayertimesapp.R
import com.islamic.prayertimesapp.common.connectivityObserver.ConnectivityObserver
import com.islamic.prayertimesapp.common.connectivityObserver.NetworkConnectivityObserver
import com.islamic.prayertimesapp.common.util.Constants.Companion.COUNTDOWN_TIME_KEY
import com.islamic.prayertimesapp.presentation.countdownWidget.countdownWidget
import com.islamic.prayertimesapp.presentation.splash.notifications.PrayerNotificationWorker
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    lateinit var connectivityObserver: ConnectivityObserver
        var lost: Boolean = false
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_main)

            // إعادة جدولة العمل لإرسال إشعار عند فتح التطبيق



            // جدولة مهمة الصلاة عند فتح التطبيق
            PrayerNotificationWorker.schedulePrayerNotification(this)

//            schedulePrayerNotification(this)
//            PrayerNotificationWorker.schedulePrayerNotification(applicationContext)
            window.statusBarColor = ContextCompat.getColor(this, R.color.black)
            checkNetwork()

        }




        fun checkNetwork() {


            connectivityObserver = NetworkConnectivityObserver(applicationContext)
            connectivityObserver.observe().onEach { it ->
                if (it.toString() == "Lost" || it.toString() == "Losing" || it.toString() == "Unavailable") {
                    //start destination
                    Toast.makeText(applicationContext, "lost Connection", Toast.LENGTH_LONG).show()
                    lost = true

                } else {
                    if (lost)
                        Toast.makeText(applicationContext, "back online", Toast.LENGTH_LONG).show()

                }
            }.launchIn(lifecycleScope)
        }

    }