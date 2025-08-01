package com.example.organ_donation_app

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

class HospitalDashboardActivity : AppCompatActivity() {

    private lateinit var donationListLayout: LinearLayout
    private lateinit var firestore: FirebaseFirestore

    private lateinit var buttonViewDonationHistory: Button
    private lateinit var buttonViewMedicalHistory: Button
    private lateinit var buttonLogout2: Button
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hospital_dashboard)

        firestore = FirebaseFirestore.getInstance()
        donationListLayout = findViewById(R.id.donationHistoryList)

        buttonViewDonationHistory = findViewById(R.id.buttonViewDonationHistory)
        buttonViewMedicalHistory = findViewById(R.id.buttonViewMedicalHistory)
        buttonLogout2 = findViewById(R.id.buttonLogout2)
        buttonViewDonationHistory.setOnClickListener {
            startActivity(Intent(this, AllDonationsActivity::class.java))
        }

        buttonViewMedicalHistory.setOnClickListener {
            startActivity(Intent(this, AllMedicalHistoryActivity::class.java))
        }
        buttonLogout2.setOnClickListener {
            auth.signOut()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }


}
