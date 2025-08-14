package com.example.organ_donation_app

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.google.firebase.auth.FirebaseAuth

class HospitalDashboardActivity : AppCompatActivity() {

    private lateinit var cardViewHistory: CardView
    private lateinit var cardViewOrganList: CardView
    private lateinit var cardViewLogout: CardView
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hospital_dashboard)

        auth = FirebaseAuth.getInstance()

        cardViewHistory = findViewById(R.id.cardViewDonations)
        cardViewOrganList = findViewById(R.id.cardViewOrgans)
        cardViewLogout = findViewById(R.id.cardLogout)

        cardViewHistory.setOnClickListener {
            startActivity(Intent(this, AllDonationsActivity::class.java))
        }

        cardViewOrganList.setOnClickListener {
            startActivity(Intent(this, AllMedicalHistoryActivity::class.java))
        }


        cardViewLogout.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }
}
