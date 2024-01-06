package com.example.isepchat.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.example.isepchat.R
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class AuthentificationActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    lateinit var tvRegister: TextView
    lateinit var textInputLayoutEmail: TextInputLayout
    lateinit var textInputLayoutPassword: TextInputLayout
    lateinit var btnConnect: MaterialButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_authentification)

        // Initialize Firebase Auth
        auth = Firebase.auth

        tvRegister = findViewById(R.id.tvRegister)
        textInputLayoutEmail = findViewById(R.id.textInputLayoutEmail)
        textInputLayoutPassword = findViewById(R.id.textInputLayoutPassword)
        btnConnect = findViewById(R.id.btnConnect)

    }

    override fun onStart() {
        super.onStart()

        tvRegister.setOnClickListener {
            Intent(this, RegisterActivity::class.java).also {
                startActivity(it)
            }
        }

        btnConnect.setOnClickListener {

            textInputLayoutEmail.isErrorEnabled = false
            textInputLayoutPassword.isErrorEnabled = false

            val email = textInputLayoutEmail.editText?.text.toString()
            val password = textInputLayoutPassword.editText?.text.toString()

            if(email.isEmpty() || password.isEmpty()) {
                if(password.isEmpty()) {
                    textInputLayoutPassword.error = "Password is required!"
                    textInputLayoutPassword.isErrorEnabled = true
                }
                if(email.isEmpty()) {
                    textInputLayoutEmail.error = "Email is required!"
                    textInputLayoutEmail.isErrorEnabled = true
                }
            } else {
                signIn(email, password)
            }
        }
    }

    fun signIn(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if(task.isSuccessful) {
                Intent(this, Main::class.java).also {
                    startActivity(it)
                }
                finish()
            } else {
                textInputLayoutPassword.error = "Authentication failed!"
                textInputLayoutEmail.isErrorEnabled = true
                textInputLayoutPassword.isErrorEnabled = true
            }
        }


    }
}