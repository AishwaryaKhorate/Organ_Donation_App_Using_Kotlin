package com.example.organ_donation_app

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class ApprovedActivity : AppCompatActivity() {

    private lateinit var buttonContinue: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_approved)

        buttonContinue = findViewById(R.id.buttonContinue)

        // Set up the click listener for the button
        buttonContinue.setOnClickListener {
            // Create an Intent to navigate to the DashboardActivity
            val intent = Intent(this, DonorDashboardActivity::class.java)
            startActivity(intent)

            // Optional: Close the current activity so the user can't go back
            finish()
        }
    }
}