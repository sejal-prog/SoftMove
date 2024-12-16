package com.example.softmove.Views

import android.animation.AnimatorInflater
import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.softmove.MainActivity
import com.example.softmove.R
import com.example.softmove.databinding.ActivityLoginBinding
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class Login : AppCompatActivity() {
    private lateinit var binding : ActivityLoginBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        // card animation
        val animator = AnimatorInflater.loadAnimator(this, R.animator.topanime)
        animator.setTarget(binding.signImg)
        animator.start()

        FirebaseApp.initializeApp(this)
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()

        binding.signup.setOnClickListener(){
            intent= Intent(this, Signup::class.java)
            startActivity(intent)
        }
        binding.loginbtn.setOnClickListener(){
            val email:String = binding.loginEmail.getText().toString()
            val password:String=binding.loginPassword.getText().toString()
            validateCredentials(email,password)
        }

    }



    fun validateCredentials(emailID:String,password:String) {
        auth = FirebaseAuth.getInstance()
        val progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Signing In...")
        progressDialog.show()
        if (emailID.isNotEmpty() && password.isNotEmpty()) {
            auth.signInWithEmailAndPassword(emailID, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        progressDialog.hide()
                        val userRef = database.getReference("users")
                        val emailRef = userRef.orderByChild("email").equalTo(emailID)

                        emailRef.addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                if (snapshot.exists()) {
                                    for (data in snapshot.children) {
                                        val name = data.child("name").value.toString()
                                        // Pass the name to the next activity via an intent
                                        intent = Intent(applicationContext, MainActivity::class.java)
                                        intent.putExtra("name", name)
                                        startActivity(intent)
                                    }
                                } else {
                                    // Email not found in the database, display error message
                                    Toast.makeText(applicationContext, "Email not found.", Toast.LENGTH_SHORT).show()
                                }
                            }

                            override fun onCancelled(error: DatabaseError) {
                                // Database error occurred, display error message
                                Toast.makeText(applicationContext, "Database error.", Toast.LENGTH_SHORT).show()
                            }
                        })


                    } else {
                        progressDialog.hide()
                        Toast.makeText(applicationContext, "Invalid Credentials", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
        } else {
            Toast.makeText(
                this, "Please enter email and password.",
                Toast.LENGTH_SHORT
            ).show()
        }

    }


}