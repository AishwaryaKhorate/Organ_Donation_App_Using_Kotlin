package com.example.organdonation

import android.os.Bundle
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.organ_donation_app.R

class ImpactTrackerActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_impact_tracker)

        val circularProgress = findViewById<ProgressBar>(R.id.circularProgress)
        val tvLivesCount = findViewById<TextView>(R.id.tvLivesCount)

        // Example dynamic data
        val livesSaved = 4
        circularProgress.progress = livesSaved * 25 // Max 4 lives = 100%
        tvLivesCount.text = "$livesSaved"
    }
}