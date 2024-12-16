package com.example.softmove

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.softmove.databinding.ActivityExerciseCompleteBinding
import com.example.softmove.databinding.ActivityMeditationBreathingBinding
import java.io.ByteArrayOutputStream

class Exercise_complete : AppCompatActivity() {

    private lateinit var binding:ActivityExerciseCompleteBinding
    public lateinit var exerciseType:String
    public lateinit var exerciseName:String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityExerciseCompleteBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        exerciseName = intent.getStringExtra("EXERCISE_NAME").toString()
        exerciseType = intent.getStringExtra("EXERCISE_TYPE").toString()



        binding.completeNextbtn.setOnClickListener{
            callIntent()
        }

    }

    private fun callIntent() {




        if(exerciseType=="Yoga"){
            val drawableId = R.drawable.menus_yoga
            val bitmap = BitmapFactory.decodeResource(resources, drawableId)
            val outputStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream)
            val compressedImageBytes: ByteArray = outputStream.toByteArray()
            val intent= Intent(this,Excercises_screen::class.java)
            intent.putExtra("text",exerciseType)
            intent.putExtra("image", compressedImageBytes)
            startActivity(intent)
            finish()
        }else{
            val drawableId = R.drawable.menus_streching
            val bitmap = BitmapFactory.decodeResource(resources, drawableId)
            val outputStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream)
            val compressedImageBytes: ByteArray = outputStream.toByteArray()
            val intent= Intent(this,Excercises_screen::class.java)
            intent.putExtra("text",exerciseType)
            intent.putExtra("image",compressedImageBytes)
            startActivity(intent)
            finish()
        }
    }
}