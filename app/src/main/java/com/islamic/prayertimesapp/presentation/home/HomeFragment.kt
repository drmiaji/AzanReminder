package com.islamic.prayertimesapp.presentation.home

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.islamic.prayertimesapp.R
import com.islamic.prayertimesapp.common.util.Constants.Companion.COUNTDOWN_TIME_KEY
import com.islamic.prayertimesapp.common.util.Resource
import com.islamic.prayertimesapp.common.util.formatTimeTo12Hour
import com.islamic.prayertimesapp.data.models.PrayerTimeResponse
import com.islamic.prayertimesapp.databinding.FragmentHomeBinding
import com.islamic.prayertimesapp.presentation.home.notifications.PrayerNotificationWorkerHome
import com.islamic.prayertimesapp.presentation.home.viewmodel.HomeViewModel
import com.islamic.prayertimesapp.presentation.home.viewmodel.LocationHelper
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.Month
import java.util.*

@RequiresApi(Build.VERSION_CODES.O)
@AndroidEntryPoint
class HomeFragment : Fragment(R.layout.fragment_home) {

    private val viewModel: HomeViewModel by activityViewModels()
    lateinit var binding: FragmentHomeBinding
    private val currentDate = LocalDate.now()
    private var currentDay = currentDate.dayOfMonth
    private var currentMonth = currentDate.month
    private var currentYear = currentDate.year
    private lateinit var prayerTimeList: List<String>
    private lateinit var locationHelper: LocationHelper
    private var firstTime = 0
    private var index = 0
    private var latitude: Double = 0.0
    private var longitude: Double = 0.0
    private lateinit var prayersTimes: PrayerTimeResponse

    companion object {
        const val LOCATION_PERMISSION_REQUEST_CODE = 100
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onResume() {
        super.onResume()
        // استدعاء لتشغيل الإشعار بشكل دوري عند استئناف الـ Fragment
        context?.let { PrayerNotificationWorkerHome.schedulePrayerNotification(it) }

    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        locationHelper = LocationHelper(requireActivity() as AppCompatActivity)

        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQUEST_CODE)
        } else {
            getUserLocation()
        }

        calendarHandler()
        initObservation()


        binding.QiblaBtn.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_qiblaFragment)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun initObservation() {
        with(viewModel) {
            // ملاحظة البيانات المتعلقة بأوقات الصلاة
            getPrayerTimeState.observe(viewLifecycleOwner) { response ->
                when (response) {
                    is Resource.Success -> {
                        response.data?.let {
                            val prayTime = it.data[currentDay - 1].timings
                            prayerTimeList = listOf(
                                prayTime.Fajr.formatTimeTo12Hour(),
                                prayTime.Sunrise.formatTimeTo12Hour(),
                                prayTime.Dhuhr.formatTimeTo12Hour(),
                                prayTime.Asr.formatTimeTo12Hour(),
                                prayTime.Maghrib.formatTimeTo12Hour(),
                                prayTime.Isha.formatTimeTo12Hour()
                            )
                            // تحديث أوقات الصلاة في ال ViewModel
                            viewModel.setPrayerTimes(prayerTimeList)
                            // تحديث الواجهة
                            updateUI(it)
                            // حذف البيانات السابقة إذا كانت موجودة
                            viewModel.deleteAll()
                            // حفظ البيانات الجديدة
                            viewModel.saveAllPrayersTimes(it)
                        }
                    }
                    is Resource.Error -> {
                        // تحقق من أن prayersTimes تم تهيئتها قبل الوصول إليها
                        if (::prayersTimes.isInitialized) {
                            // تحديث الواجهة باستخدام البيانات من prayersTimes
                            updateUI(prayersTimes)
                        }
                    }
                    is Resource.Loading -> {
                        // هنا يمكنك معالجة حالة التحميل إذا كان ذلك مطلوبًا

                    }
                }
            }

            // ملاحظة البيانات المخزنة في ViewModel
            viewModel.getAllPrayersTimes().observe(viewLifecycleOwner) { response ->
                if (response != null) {
                    prayersTimes = response
                }
            }


            // ملاحظة التغييرات في أوقات الصلاة في ViewModel
            viewModel.prayerTimes.observe(viewLifecycleOwner) {
                if (firstTime == 0) {
                    firstTime++
                    // حساب وقت الصلاة التالي
                    val time = nextPrayer(it)
                    // حفظ الوقت المتبقي للصلاة
                    saveCountdownEndTime(requireContext(), time)
                    // بدء العد التنازلي
                    startCountdown(time / 1000)
                }
            }

            // ملاحظة التغييرات في الإحداثيات الجغرافية
            viewModel.lat.observe(viewLifecycleOwner) { lat ->
                viewModel.long.observe(viewLifecycleOwner) { long ->
                    viewModel.getPrayerTimes(currentYear, currentMonth.value, lat, long, 1)
                    latitude = lat
                    longitude = long
                }
            }

            // ملاحظة التغييرات في فهرس الصلاة
            // to handle count down
            viewModel.index.observe(viewLifecycleOwner) { index ->

                when (index) {
                    1 -> {
                        binding.tvNextpray.text = "Fajr"
                    }

                    2 -> {
                        binding.tvNextpray.text = "Sunrise"
                    }

                    3 -> {
                        binding.tvNextpray.text = "Duhr"
                    }

                    4 -> {
                        binding.tvNextpray.text = "Asr"
                    }

                    5 -> {
                        binding.tvNextpray.text = "Maghrib"
                    }

                    6 -> {
                        binding.tvNextpray.text = "Isha"
                    }
                }
            }
        }
    }


    @SuppressLint("SetTextI18n")
    fun calendarHandler() {
        binding.tvDate.text = "$currentMonth $currentDay $currentYear"
        binding.btnIncrement.setOnClickListener {
            viewModel.getPrayerTimes(currentYear, currentMonth.value, latitude, longitude, 1)
            incrementLogic()
        }
        binding.decrementBtn.setOnClickListener {
            viewModel.getPrayerTimes(currentYear, currentMonth.value, latitude, longitude, 1)
            decrementLogic()
        }
    }


    internal fun saveCountdownEndTime(context: Context, endTimeMillis: Long) {
        val preferences: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        preferences.edit().putLong(COUNTDOWN_TIME_KEY, endTimeMillis).apply()
    }

    private var job: Job? = null

    @SuppressLint("SetTextI18n")
    fun startCountdown(totalSeconds: Long) {
        job = CoroutineScope(Dispatchers.Main).launch {
            for (seconds in totalSeconds downTo 0) {
                // تحديث واجهة المستخدم بشكل فوري
                val formattedTime = formatTime(seconds)
                binding.tvCountdown.text = "          Time Left \n $formattedTime"
            }
            // عند انتهاء العد التنازلي
            binding.tvCountdown.text = "Countdown finished for today will start at 12 am"
        }
    }


    private fun formatTime(seconds: Long): String {
        val hours = seconds / 3600
        val minutes = (seconds % 3600) / 60
        val secondsRemaining = seconds % 60
        return String.format("%02d hr %02d min %02d sec", hours, minutes, secondsRemaining)
    }

//    private fun convertTimeToMilliseconds(timeString: String): Long {
//        return try {
//            val format = SimpleDateFormat("hh:mm a", Locale.US)
//            val calendar = Calendar.getInstance()
//            val date = format.parse(timeString)
//            if (date != null) {
//                calendar.time = date
//                // اجعل التاريخ الحالي يتضمن وقت الصلاة
//                val prayerTimeCalendar = Calendar.getInstance()
//                prayerTimeCalendar.set(Calendar.HOUR_OF_DAY, calendar.get(Calendar.HOUR_OF_DAY))
//                prayerTimeCalendar.set(Calendar.MINUTE, calendar.get(Calendar.MINUTE))
//                prayerTimeCalendar.set(Calendar.SECOND, 0)
//                prayerTimeCalendar.timeInMillis
//            } else {
//                -1
//            }
//        } catch (e: Exception) {
//            -1
//        }
//    }

    private fun convertTimeToMilliseconds(timeString: String): Long {
        return try {
            // التنسيق باستخدام 12 ساعة مع AM/PM
            val format12Hour = SimpleDateFormat("hh:mm a", Locale.US)
            val date12Hour = format12Hour.parse(timeString)

            if (date12Hour != null) {
                return date12Hour.time
            }

            // إذا لم يكن التوقيت بنظام 12 ساعة، حاول استخدام 24 ساعة
            val format24Hour = SimpleDateFormat("HH:mm", Locale.US)
            val date24Hour = format24Hour.parse(timeString)

            date24Hour?.time ?: -1
        } catch (e: Exception) {
            -1
        }
    }


    private fun nextPrayer(times: List<String>): Long {
        val currentTime = Calendar.getInstance().time
        val dateFormat = SimpleDateFormat("hh:mm a", Locale.getDefault()) // تنسيق الوقت باستخدام AM/PM
        val formattedTime = dateFormat.format(currentTime)

        val newTime = convertTimeToMilliseconds(formattedTime) // الوقت الحالي بالميلي ثانية

        var prayerTimeMillis: Long = 0
        var index = -1 // الفهرس الذي سيتم تحديده بناءً على الصلاة القادمة

        // المرور عبر كل أوقات الصلاة
        for ((i, prayerTime) in times.withIndex()) {
            val prayerTimeMillis = convertTimeToMilliseconds(prayerTime) // تحويل وقت الصلاة للميلي ثانية

            // إذا كان وقت الصلاة بعد الوقت الحالي
            if (newTime < prayerTimeMillis) {
                index = i // تحديد الفهرس للصلاة القادمة
                val timeRemaining = prayerTimeMillis - newTime // حساب الوقت المتبقي للصلاة القادمة
                viewModel.setIndex(index) // تعيين الفهرس للصلاة القادمة
                return timeRemaining // إرجاع الوقت المتبقي
            }
        }

        // إذا كانت جميع الصلوات قد مرت اليوم، يمكن إضافة منطق لإعادة الحساب لبداية اليوم التالي
        return 0 // إذا لم يكن هناك أي صلاة قادمة
    }

//    private fun nextPrayer(times: List<String>): Long {
//        val currentTime = Calendar.getInstance().timeInMillis
//
//
//        for (i in times.indices) {
//            val prayerTimeInMillis = convertTimeToMilliseconds(times[i])
//            if (prayerTimeInMillis > currentTime) {
//                index = i + 1
//                viewModel.setIndex(index)
//                return prayerTimeInMillis - currentTime
//            }
//        }
//
//        // حساب الوقت المتبقي لصلاة الفجر لليوم التالي
//        val firstPrayerInMillis = convertTimeToMilliseconds(times[0]) + 24 * 60 * 60 * 1000
//        index = 1
//        viewModel.setIndex(index)
//        return firstPrayerInMillis - currentTime
//    }

    fun updateNextPrayer(times: List<String>) {
        val timeLeft = nextPrayer(times)
        startCountdown(timeLeft / 1000) // تحويل المللي ثانية إلى ثواني
    }


    private fun updateUI(data: PrayerTimeResponse) {
        with(binding) {
            val prayTime = data.data[currentDay - 1].timings
            tvFajr.text = prayTime.Fajr.formatTimeTo12Hour()
            tvSunrise.text = prayTime.Sunrise.formatTimeTo12Hour()
            tvDuhr.text = prayTime.Dhuhr.formatTimeTo12Hour()
            tvAsr.text = prayTime.Asr.formatTimeTo12Hour()
            tvMaghrib.text = prayTime.Maghrib.formatTimeTo12Hour()
            tvIsha.text = prayTime.Isha.formatTimeTo12Hour()
        }
    }

    private fun getUserLocation() {
        locationHelper.fetchLocation(object : LocationHelper.OnLocationFetchedListener {
            override fun onLocationFetched(location: Location?, address: String?) {
                if (location != null) {
                    viewModel.setLat(location.latitude)
                    viewModel.setLong(location.longitude)
                    binding.tvLocation.text = address
                    viewModel.getPrayerTimes(currentYear, currentMonth.value, location.latitude, location.longitude, 1)
                } else {
                    binding.tvLocation.text = "Error fetching location"
                }
            }

            @SuppressLint("SetTextI18n")
            override fun onError(error: String?) {
                binding.tvLocation.text = "$error please restart the app"
            }
        })
    }


    @SuppressLint("SetTextI18n")
    private fun incrementLogic() {
        if (currentDay < currentMonth.maxLength()) {
            currentDay += 1
            binding.tvDate.text = "$currentMonth  $currentDay $currentYear"
            viewModel.setDay(currentDay)
        } else {
            if (currentMonth == Month.DECEMBER && currentDay == currentMonth.maxLength()) {
                currentYear += 1
                viewModel.setYear(currentYear)
            }
            currentMonth += 1
            viewModel.setMonth(currentMonth.value)
            currentDay = 1
            viewModel.setDay(currentDay)
            binding.tvDate.text = "$currentMonth  $currentDay $currentYear"
        }
    }

    @SuppressLint("SetTextI18n")
    private fun decrementLogic() {
        if (currentDay > 1) {
            currentDay -= 1
            binding.tvDate.text = "$currentMonth $currentDay $currentYear"
            viewModel.setDay(currentDay)
        } else {
            if (currentMonth == Month.JANUARY && currentDay == 1) {
                currentYear -= 1
                viewModel.setYear(currentYear)
            }
            currentMonth -= 1
            viewModel.setMonth(currentMonth.value)
            currentDay = currentMonth.maxLength()
            viewModel.setDay(currentDay)
            binding.tvDate.text = "$currentMonth  $currentDay $currentYear"
        }
    }


}
