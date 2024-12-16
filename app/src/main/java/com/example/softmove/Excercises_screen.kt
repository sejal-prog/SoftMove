package com.example.softmove

import android.content.ContentValues.TAG
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.softmove.Adapters.ExerciseAdapter
import com.example.softmove.Models.Exercises
import com.example.softmove.databinding.ActivityExcercisesScreenBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.io.ByteArrayOutputStream


class Excercises_screen : AppCompatActivity() {

    private lateinit var binding:ActivityExcercisesScreenBinding
    private  val TAG = "ExercisesActivity"
    private lateinit var excercisetype:String
    //private lateinit var exerciseList: MutableList<Exercises>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityExcercisesScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()


        // Get compressed image data from the intent
        val compressedImageBytes: ByteArray? = intent.getByteArrayExtra("image")
        // Decode compressed image data into Bitmap object
        val compressedBitmap: Bitmap = BitmapFactory.decodeByteArray(compressedImageBytes, 0, compressedImageBytes!!.size)
        // Load the Bitmap into an ImageView using Glide
        Glide.with(this)
            .load(compressedBitmap)
            .into(binding.excercisesScreenImage)

        // Set the Bitmap to an ImageView
        //binding.excercisesScreenImage.setImageBitmap(bitmap)
        val intent = intent
        val text = intent.getStringExtra("text")
        binding.excercisesScreenExcerciseName.text=text

        if(binding.excercisesScreenExcerciseName.text=="Yoga")
        {
            binding.excercisesScreenExcerciseDesc.text= getApplicationContext().getResources().getString(R.string.yoga_desc);
            excercisetype="Yoga"
        }
        else if(binding.excercisesScreenExcerciseName.text=="Stretching"){
            binding.excercisesScreenExcerciseDesc.text=getApplicationContext().getResources().getString(R.string.Streching_desc);
            excercisetype="Stretching"
        }
        else{
            binding.excercisesScreenExcerciseDesc.text="NULL"
            excercisetype="NULL"
        }

        binding.exercisesBackbtn.setOnClickListener {
           val intent=Intent(this,MainActivity::class.java)
            startActivity(intent)
        }

        val database = Firebase.database.reference
        val exercisesRef = database.child("Excercises")
        val exerciseRef = exercisesRef.child(excercisetype)
        //exerciseList = mutableListOf()
        val layoutManager = LinearLayoutManager(this)
        binding.excercisesRecyclerview.layoutManager = layoutManager
        val exercisesList = ArrayList<Exercises>()
        exerciseRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                for (exerciseSnapshot in snapshot.children) {
                    val exercise = exerciseSnapshot.getValue(Exercises::class.java)
                    exercisesList.add(exercise!!)
                }
                Log.i("IMPPPPPP::::::", exercisesList.toString())
                val adapter = ExerciseAdapter(exercisesList)
                binding.excercisesRecyclerview.adapter = adapter

                adapter.setOnItemClickListener { exercisesList ->
                    // handle the click event here
                    val i = Intent(applicationContext, Excercise_description::class.java)

                    i.putExtra("EXERCISE_NAME", exercisesList.Exercise_name)
                    i.putExtra("EXERCISE_TIME", exercisesList.Exercise_time)
                    i.putExtra("EXERCISE_TYPE",excercisetype)
                    // pass other exercise data as needed
                    startActivity(i)
                }

            }

            override fun onCancelled(error: DatabaseError) {
                Log.w(TAG, "loadPost:onCancelled", error.toException())
            }
        })

    }
}