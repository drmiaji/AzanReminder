package com.islamic.prayertimesapp.presentation.splash

import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.islamic.prayertimesapp.R
import com.islamic.prayertimesapp.databinding.FragmentSplashBinding
import com.islamic.prayertimesapp.presentation.splash.notifications.PrayerNotificationWorker

class SplashFragment : Fragment(R.layout.fragment_splash) {

    private var _binding: FragmentSplashBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSplashBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // إرسال إشعار الصلاة على النبي عند فتح التطبيق
        sendPrayerNotification()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // إخفاء شريط التنقل وشريط الحالة
            activity?.window?.decorView?.systemUiVisibility = (
                    View.SYSTEM_UI_FLAG_HIDE_NAVIGATION          // يخفي شريط التنقل (Navigation Bar)
                            or View.SYSTEM_UI_FLAG_FULLSCREEN            // يخفي شريط الحالة (Status Bar)
                            or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY      // يجعل واجهة النظام تختفي تلقائيًا مع إمكانية العودة بإيماءة
                    )

            // إخفاء Action Bar (إذا كان موجودًا)
            activity?.actionBar?.hide()
        }

        // تشغيل الأنيميشن
        binding.lottieAnimation.playAnimation()

        // تأخير الانتقال
        Handler(Looper.getMainLooper()).postDelayed({
            findNavController().navigate(R.id.action_splashFragment2_to_homeFragment)
        }, 8500)
    }

    // دالة لإرسال إشعار الصلاة على النبي باستخدام WorkManager
    private fun sendPrayerNotification() {
        // إعداد عمل واحد (OneTimeWorkRequest) لإرسال إشعار الصلاة على النبي
        val workRequest = OneTimeWorkRequest.Builder(PrayerNotificationWorker::class.java)
            .setInitialDelay(1, java.util.concurrent.TimeUnit.SECONDS) // تأخير بداية العمل لمدة ثانية واحدة
            .build()

        // تنفيذ العمل عبر WorkManager
        WorkManager.getInstance(requireContext()).enqueue(workRequest)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // تجنب تسرب الذاكرة
    }
}
