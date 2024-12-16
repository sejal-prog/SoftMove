package com.example.softmove

import android.os.Bundle
import android.content.Intent
import android.speech.tts.TextToSpeech
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.softmove.Movenetmodel.poseestimation.PoseDetection
import com.example.softmove.databinding.ActivityExcercisesScreenBinding
import pl.droidsonroids.gif.GifDrawable
import pl.droidsonroids.gif.GifImageView
import java.io.InputStream
import java.util.*

class ExerciseDemo : AppCompatActivity() {
    private lateinit var tts: TextToSpeech
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_exercise_demo)

        val gifImageView = findViewById<GifImageView>(R.id.gifImageView)
        val startButton = findViewById<Button>(R.id.startExercisebutton)

        val exercise_name = intent.getStringExtra("EXERCISE_NAME")
        val exerciseType = intent.getStringExtra("EXERCISE_TYPE")

        var gifInputStream: InputStream = applicationContext.assets.open("tree_pose.gif")

        tts = TextToSpeech(applicationContext, TextToSpeech.OnInitListener {
            if(it == TextToSpeech.SUCCESS) {
                tts.language = Locale.US
                tts.setSpeechRate(0.8F)
                tts.speak("Start doing" + exercise_name +"exercise as shown in demo.", TextToSpeech.QUEUE_ADD, null)
            }
        })

        when (exercise_name) {
            "Vrikshasana" -> {
                gifInputStream = applicationContext.assets.open("tree_pose.gif")
            }
            "Virabhadrasana" -> {
                gifInputStream = applicationContext.assets.open("warrior_pose.gif")
            }
            "Bhujangasana" -> {
                gifInputStream = applicationContext.assets.open("cobra_pose.gif")
            }
            "Utkatasana" -> {
                gifInputStream = applicationContext.assets.open("chair_pose.gif")
            }
            "Adho Mukha Shvanasana" -> {
                gifInputStream = applicationContext.assets.open("dog_pose.gif")
            }
            "Right Hand Stretching" -> {
                gifInputStream = applicationContext.assets.open("hand_stretching.gif")
            }
            "Left Hand Stretching" -> {
                gifInputStream = applicationContext.assets.open("hand_stretching.gif")
            }
            "Right Leg Stretching" -> {
                gifInputStream = applicationContext.assets.open("right_leg_stretching.gif")
            }
            "Left Leg Stretching" -> {
                gifInputStream = applicationContext.assets.open("left_leg_stretching.gif")
            }
            "Calf Stretching" -> {
                gifInputStream = applicationContext.assets.open("calfStretch.gif")
            }
        }

        val gifDrawable = GifDrawable(gifInputStream)
        gifImageView.setImageDrawable(gifDrawable)

        startButton.setOnClickListener {
            val ins= Intent(applicationContext, PoseDetection::class.java)
            ins.putExtra("EXERCISE_NAME",exercise_name)
            ins.putExtra("EXERCISE_TYPE",exerciseType)
            startActivity(ins)
        }
    }
}