package com.example.softmove.Views

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import com.example.softmove.databinding.ActivitySignupBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class Signup : AppCompatActivity(){

    private lateinit var binding : ActivitySignupBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()

        supportActionBar?.hide()
        binding.backBtn.setOnClickListener(){
            intent= Intent(this, Login::class.java)
            startActivity(intent)
        }

        binding.registerbtn.setOnClickListener{
            val name = binding.registerName.text.toString().trim()
            val email = binding.registerEmail.text.toString().trim()
            val password=binding.registerPassword.text.toString().trim()
            val confirmpassword=binding.registerConfirmpassword.text.toString().trim()

            if (name.isEmpty()) {
                binding.registerName.error = "Name is required"
                binding.registerName.requestFocus()
                return@setOnClickListener
            }

            if (email.isEmpty()) {
                binding.registerEmail.error = "Email is required"
                binding.registerEmail.requestFocus()
                return@setOnClickListener
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                binding.registerEmail.error = "Invalid email"
                binding.registerEmail.requestFocus()
                return@setOnClickListener
            }

            if (password.isEmpty()) {
                binding.registerPassword.error = "Password is required"
                binding.registerPassword.requestFocus()
                return@setOnClickListener
            }

            if (password.length < 6) {
                binding.registerPassword.error = "Password must be at least 6 characters"
                binding.registerPassword.requestFocus()
                return@setOnClickListener
            }

            if (confirmpassword.isEmpty()) {
                binding.registerConfirmpassword.error = "Confirm password is required"
                binding.registerConfirmpassword.requestFocus()
                return@setOnClickListener
            }

            if (confirmpassword != password) {
                binding.registerConfirmpassword.error = "Passwords do not match"
                binding.registerConfirmpassword.requestFocus()
                return@setOnClickListener
            }

            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Registration successful, store user data in the database
                        val user = auth.currentUser
                        val userId = user!!.uid
                        val userData = hashMapOf(
                            "name" to name,
                            "email" to email
                        )
                        database.reference.child("users").child(userId).setValue(userData)
                        Toast.makeText(
                            this, "Registration Successful.",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        // Registration failed, display error message
                        Toast.makeText(
                            this, "Registration failed.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

        }
    }
}