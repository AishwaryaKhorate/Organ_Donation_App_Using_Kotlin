package com.example.organ_donation_app

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity

import com.google.firebase.auth.FirebaseAuth

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            // User already logged in → go to Dashboard
            startActivity(Intent(this, DonorDashboardActivity::class.java))
        } else {
            // User not logged in → go to Login
            startActivity(Intent(this, LoginActivity::class.java))
        }
        finish()
    }
}
