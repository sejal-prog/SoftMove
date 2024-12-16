package com.example.softmove

import android.content.Context
import android.content.DialogInterface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.speech.tts.TextToSpeech
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import com.example.softmove.ViewModel.HomeViewModel
import com.example.softmove.databinding.ActivityBreathBinding
import com.example.softmove.databinding.ActivityMeditationBreathingBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import java.util.*
import java.util.concurrent.TimeUnit

class BreathActivity : AppCompatActivity() {
    private lateinit var textIndicator: TextView
    private lateinit var timer: CountDownTimer
    var isRunning = false
    private lateinit var viewModel: HomeViewModel
    lateinit var currentUser: FirebaseUser
    var minutes = 3L
    private lateinit var tts: TextToSpeech
    private lateinit var binding: ActivityBreathBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBreathBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        currentUser = FirebaseAuth.getInstance().currentUser!!
        val uid = FirebaseAuth.getInstance().uid
        viewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
        viewModel.init(applicationContext, uid!!)

        textIndicator = binding.indicator
        timer = createTimer()

        binding.start.setOnClickListener {
            toggle()
        }

        binding.close.setOnClickListener {
            showDialog(this)
        }
    }

    private fun toggle() {
        if(isRunning) {
            stopExercise()
            binding.start.text = "Start"
        } else {
            binding.breathe.playAnimation()
            binding.start.text = getString(R.string.str_end)
            timer.start()
        }
    }

    private fun createTimer(): CountDownTimer {
        return object: CountDownTimer(3 * 60000, 1000) {
            var sec = 0L
            override fun onTick(ms: Long) {
                isRunning = true
                minutes = TimeUnit.MILLISECONDS.toMinutes(ms) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(ms))
                sec = TimeUnit.MILLISECONDS.toSeconds(ms) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(ms))

                textIndicator.text = "${minutes.toString().padStart(2, '0')}:${sec.toString().padStart(2, '0')}"

                if(minutes == 2L && sec == 57L) {
                    tts = TextToSpeech(applicationContext, TextToSpeech.OnInitListener {
                        if(it == TextToSpeech.SUCCESS) {
                            tts.language = Locale.US
                            tts.setSpeechRate(0.8F)
                            tts.speak("Inhale through your nose and exhale through your mouth", TextToSpeech.QUEUE_ADD, null)
                        }
                    })
                }
            }

            override fun onFinish() {
                stopExercise()
            }
        }
    }

    fun stopExercise() {
        binding.breathe.pauseAnimation()
        isRunning = false
        timer.cancel()
    }

    private fun showDialog(context: Context){
        val builder: AlertDialog.Builder = AlertDialog.Builder(context)

        builder.setMessage("Do you want to stop breathing exercise ?").setCancelable(true)
        builder.setPositiveButton("Yes", DialogInterface.OnClickListener { dialog, which ->
            finish()
        })

        builder.setNegativeButton("Cancel", DialogInterface.OnClickListener { dialog, which -> dialog.cancel() })
        val alert = builder.create()
        alert.setTitle("Are you sure")
        alert.show()
    }

    override fun onStop() {
        super.onStop()

        viewModel.updateBreatheMin((3L - minutes).toInt())
        viewModel.updateBreatheCount()
        timer.cancel()
    }
}