package com.example.softmove

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.viewbinding.ViewBinding
import com.example.softmove.ViewModel.HomeViewModel
import com.example.softmove.databinding.ActivityMainBinding
import com.example.softmove.databinding.ActivityMeditationBreathingBinding
import com.example.softmove.databinding.ReportBinding
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.miu.meditationapp.helper.NotificationReceiver
import java.util.Calendar

class Mediatationbreathing : AppCompatActivity() {

    lateinit var currentUser: FirebaseUser
    private lateinit var alarmManager: AlarmManager
    private lateinit var calendar: Calendar
    private lateinit var picker: MaterialTimePicker
    private lateinit var database : DatabaseReference
    private lateinit var viewModel: HomeViewModel
    private lateinit var binding: ActivityMeditationBreathingBinding
    //private lateinit var reportbinding:ReportBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMeditationBreathingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        createNotificationChannel()
        supportActionBar?.hide()

        currentUser = FirebaseAuth.getInstance().currentUser!!
        val uid = FirebaseAuth.getInstance().uid

        viewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
        viewModel.init(applicationContext, uid!!)

        updateReportTexts()

        database = FirebaseDatabase.getInstance().getReference("users")
        database.child(uid!!).get().addOnSuccessListener {
            if (it.exists()) {
                binding.textView3.text = it.child("name").value.toString()
            } else {
                Toast.makeText(applicationContext, "Check your Internet connection.", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener {
            Toast.makeText(applicationContext, "Couldn't find the user. Internet connection have to connected properly.", Toast.LENGTH_SHORT).show()
        }

        alarmManager = applicationContext?.getSystemService(ALARM_SERVICE) as AlarmManager

        binding.button.setOnClickListener {
            startActivity(Intent(applicationContext, MeditationActivity::class.java))
        }

        binding.breathe.setOnClickListener {
            startActivity(Intent(applicationContext, BreathActivity::class.java))
        }

        updateReportProgress()

//        binding.remind.setOnClickListener {
//            picker = MaterialTimePicker.Builder()
//                .setTimeFormat(TimeFormat.CLOCK_12H)
//                .setHour(12)
//                .setMinute(0)
//                .setTitleText("Select Reminder time")
//                .build()
//
//            //activity?.let { it1 -> picker.show(it1.supportFragmentManager, "GENO") }
//
//            picker.addOnPositiveButtonClickListener {
//                cancelAlarm()
//                calendar.set(Calendar.HOUR_OF_DAY, picker.hour)
//                calendar.set(Calendar.MINUTE, picker.minute)
//                calendar.set(Calendar.SECOND, 0)
//                calendar.set(Calendar.MILLISECOND, 0)
//
//                var intent = Intent(applicationContext, NotificationReceiver::class.java)
//
//                var pendingIntent: PendingIntent = PendingIntent.getBroadcast(
//                    this,
//                    200,
//                    intent,
//                    PendingIntent.FLAG_IMMUTABLE
//                )
//
//                var alarmManager: AlarmManager = applicationContext?.getSystemService(Context.ALARM_SERVICE) as AlarmManager
//                alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, AlarmManager.INTERVAL_DAY, pendingIntent)
//                Toast.makeText(applicationContext, "Successfully set reminder at: ${calendar.get(Calendar.HOUR_OF_DAY).toString()}:${calendar.get(Calendar.MINUTE).toString()} every day.", Toast.LENGTH_SHORT).show()
//            }
//        }

    }

    private fun updateReportTexts() {

        binding.valMeditateTimes.text = viewModel.getMeditationCount().toString()
        //Log.i("IMPPPP",viewModel.getMeditationCount().toString())
        binding.valMeditate.text = viewModel.getMeditationMin().toString()
        binding.valBreatheTimes.text = viewModel.getBreatheCount().toString()
        binding.valBreathe.text = viewModel.getBreatheMin().toString()
    }

    private fun updateReportProgress() {
        val medMin = viewModel.getMeditationMin()
        val breMin = viewModel.getBreatheMin()
        val medCount = viewModel.getMeditationCount()
        val breCount = viewModel.getBreatheCount()
        var percentage = 1
        if(medCount > 0 && breCount > 0) {
            percentage = (medMin + breMin) * 100 / (medCount * 20) + (breCount * 3)
        }
        binding.statusBar.progress = percentage
        binding.valMeditateTimes.text = medCount.toString()
        binding.valMeditate.text  = medMin.toString()
        binding.valBreatheTimes.text = breCount.toString()
        binding.valBreathe.text = breMin.toString()
    }

    override fun onResume() {
        updateReportTexts()
        updateReportProgress()
        super.onResume()
    }

    fun createNotificationChannel() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            var name: CharSequence = "MORNING Meditation Channel"
            var description: String = "Channel for morning meditation remender"

            var importance: Int = NotificationManager.IMPORTANCE_DEFAULT
            var channel: NotificationChannel = NotificationChannel("MORNING", name, importance)
            channel.description = description

            var notificationManager: NotificationManager? = applicationContext?.getSystemService(
                NotificationManager::class.java)
            notificationManager?.createNotificationChannel(channel)
        }
    }

    fun cancelAlarm() {
        var intent = Intent(applicationContext, Mediatationbreathing::class.java)
        var pendingInteng = PendingIntent.getBroadcast(
            applicationContext,
            200,
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )

        if(alarmManager == null) {
            alarmManager = applicationContext?.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        }

        alarmManager.cancel(pendingInteng)
    }

}
