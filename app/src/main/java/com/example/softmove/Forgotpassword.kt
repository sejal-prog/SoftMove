package com.example.softmove

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.softmove.Views.Login
import com.example.softmove.databinding.ActivityForgotpasswordBinding
import com.example.softmove.databinding.ActivityLoginBinding
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class Forgotpassword : AppCompatActivity() {

    private lateinit var binding : ActivityForgotpasswordBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityForgotpasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        FirebaseApp.initializeApp(this)
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        val currentUser = FirebaseAuth.getInstance().currentUser
        val userEmail = currentUser?.email

        binding.changepasswordbtn.setOnClickListener {
            val password=binding.newPassword.text.toString().trim()
            val confirmpassword=binding.renternewPassword.text.toString().trim()

            if (password.isEmpty()) {
                binding.newPassword.error = "Password is required"
                binding.newPassword.requestFocus()
                return@setOnClickListener
            }

            if (password.length < 6) {
                binding.newPassword.error = "Password must be at least 6 characters"
                binding.newPassword.requestFocus()
                return@setOnClickListener
            }

            if (confirmpassword.isEmpty()) {
                binding.renternewPassword.error = "Confirm password is required"
                binding.renternewPassword.requestFocus()
                return@setOnClickListener
            }

            if (confirmpassword != password) {
                binding.renternewPassword.error = "Passwords do not match"
                binding.renternewPassword.requestFocus()
                return@setOnClickListener
            }

            currentUser?.updatePassword(password)
                ?.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                       Toast.makeText(applicationContext,"Password Updated Succesfully!!",Toast.LENGTH_SHORT).show()
                        val intent = Intent(applicationContext, Login::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        // Password update failed
                        val exception = task.exception
                        //println("Password update failed: ${exception?.message}")
                        Toast.makeText(applicationContext,"Password update failed: ${exception?.message}",Toast.LENGTH_SHORT).show()
                    }
                }

        }

    }
}