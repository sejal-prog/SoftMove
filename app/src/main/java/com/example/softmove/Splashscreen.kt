package com.example.softmove

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.softmove.Models.Splashscreenitems
import com.example.softmove.Views.Login
import com.example.softmove.databinding.ActivitySplashscreenBinding
//import kotlinx.android.synthetic.main.activity_splashscreen.*

class Splashscreen : AppCompatActivity() {
    private lateinit var binding : ActivitySplashscreenBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashscreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

       binding.gettingstarted.setOnClickListener(){
           intent = Intent(this, Login::class.java)
           startActivity(intent)
       }

        //Slider
        val slideritems = arrayListOf<Splashscreenitems>()
        val Sliderimgs = resources.obtainTypedArray(R.array.Sliderimages)
        for (i in resources.getStringArray(R.array.Slidertitles).indices) {
            slideritems.add(
                Splashscreenitems(
                    Sliderimgs.getResourceId(i, -1),
                    resources.getStringArray(R.array.Slidertitles)[i],
                    resources.getStringArray(R.array.SliderDesc)[i]
                )
            )
        }

        Sliderimgs.recycle()
        binding.viewPager.adapter = SplashScreenAdapter(slideritems)
    }
}