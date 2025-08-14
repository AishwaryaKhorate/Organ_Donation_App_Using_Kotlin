package com.example.organ_donation_app

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.google.firebase.auth.FirebaseAuth

class DonorDashboardActivity : AppCompatActivity() {

    private lateinit var cardDonate: CardView
    private lateinit var cardMedical: CardView

    private lateinit var cardLogout: CardView

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_donor_dashboard)

        auth = FirebaseAuth.getInstance()

        cardDonate = findViewById(R.id.cardDonate)
        cardMedical = findViewById(R.id.cardMedical)
        cardLogout = findViewById(R.id.cardLogout)

        cardDonate.setOnClickListener {
            startActivity(Intent(this, OrganDonationActivity::class.java))
        }

        cardMedical.setOnClickListener {
            startActivity(Intent(this, MedicalHistoryActivity::class.java))
        }



        cardLogout.setOnClickListener {
            auth.signOut()
            Toast.makeText(this, "Logged out", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }
}
