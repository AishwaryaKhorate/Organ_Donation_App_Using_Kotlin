package com.example.organ_donation_app

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var loginButton: Button
    private lateinit var registerRedirect: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        emailEditText = findViewById(R.id.editTextEmail)
        passwordEditText = findViewById(R.id.editTextPassword)
        loginButton = findViewById(R.id.buttonLogin)
        registerRedirect = findViewById(R.id.textRegister)

        loginButton.setOnClickListener {
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            auth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener {
                    val uid = auth.currentUser?.uid ?: return@addOnSuccessListener

                    firestore.collection("users").document(uid).get()
                        .addOnSuccessListener { document ->
                            if (document != null && document.exists()) {
                                val role = document.getString("role")
                                when (role) {
                                    "Donor" -> {
                                        startActivity(Intent(this, DonorDashboardActivity::class.java))
                                        finish()
                                    }
                                    "Hospital" -> {
                                        startActivity(Intent(this, HospitalDashboardActivity::class.java))
                                        finish()
                                    }
                                    else -> {
                                        Toast.makeText(this, "Invalid user role", Toast.LENGTH_SHORT).show()
                                        auth.signOut()
                                    }
                                }
                            } else {
                                Toast.makeText(this, "User data not found", Toast.LENGTH_SHORT).show()
                                auth.signOut()
                            }
                        }
                        .addOnFailureListener {
                            Toast.makeText(this, "Failed to retrieve user role", Toast.LENGTH_SHORT).show()
                        }
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Login failed: ${it.message}", Toast.LENGTH_SHORT).show()
                }
        }

        registerRedirect.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
            finish()
        }
    }
}
