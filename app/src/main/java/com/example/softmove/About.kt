package com.example.softmove

import android.animation.AnimatorInflater
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.softmove.databinding.ActivityAboutBinding
import com.example.softmove.databinding.ActivityLoginBinding

class About : AppCompatActivity() {
    private lateinit var binding:ActivityAboutBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAboutBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        // card animation
        val animator = AnimatorInflater.loadAnimator(this, R.animator.topanime)
        animator.setTarget(binding.aboutImg)
        animator.start()
    }
}