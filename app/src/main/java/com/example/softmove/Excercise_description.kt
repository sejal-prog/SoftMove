package com.example.softmove

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.util.Log
import android.widget.Toast
import com.example.softmove.Movenetmodel.poseestimation.PoseDetection
import com.example.softmove.databinding.ActivityExcerciseDescriptionBinding
import com.example.softmove.databinding.ActivityExcercisesScreenBinding
import pl.droidsonroids.gif.GifDrawable
import java.io.InputStream
import java.util.Locale


class Excercise_description : AppCompatActivity() {

    private lateinit var binding: ActivityExcerciseDescriptionBinding
    private lateinit var tts: TextToSpeech

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityExcerciseDescriptionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val exerciseName = intent.getStringExtra("EXERCISE_NAME")
        val exerciseType = intent.getStringExtra("EXERCISE_TYPE")
        val exerciseTime = intent.getStringExtra("EXERCISE_TIME")
//        val exerciseSets = intent.getIntExtra("EXERCISE_SETS",0)
//        val exerciseReps = intent.getIntExtra("EXERCISE_REPS",0)
//        val exerciseAnimation = intent.getStringExtra("EXERCISE_ANIMATION")
        //Toast.makeText(applicationContext,exerciseSets,Toast.LENGTH_SHORT).show()



//        binding.setsText.text=exerciseSets.toString()+ " Sets"
//        binding.setsDesc.text= "Each set of "+exerciseReps.toString()+" Reps"

        binding.startbutton.setOnClickListener{
            val ins= Intent(applicationContext, PoseDetection::class.java)
            ins.putExtra("EXERCISE_NAME",exerciseName)
            Log.i("EXERCISE_NAME :::: ", exerciseName.toString())
            ins.putExtra("EXERCISE_TYPE",exerciseType)
            startActivity(ins)
        }

        var gifInputStream: InputStream = applicationContext.assets.open("tree_pose.gif")

        tts = TextToSpeech(applicationContext, TextToSpeech.OnInitListener {
            if(it == TextToSpeech.SUCCESS) {
                tts.language = Locale.US
                tts.setSpeechRate(0.8F)
                tts.speak("Start doing" + exerciseName +"exercise as shown in demo.", TextToSpeech.QUEUE_ADD, null)
            }
        })

        when (exerciseName) {
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
        binding.gifImageView.setImageDrawable(gifDrawable)

    }
}