package com.example.organ_donation_app

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class DonorDashboardActivity : AppCompatActivity() {

    private lateinit var welcomeText: TextView
    private lateinit var buttonMedicalHistory: Button
    private lateinit var buttonDonateOrgan: Button
    private lateinit var buttonHistory: Button
    private lateinit var buttonLogout: Button

    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_donor_dashboard)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        welcomeText = findViewById(R.id.textWelcome)
        buttonMedicalHistory = findViewById(R.id.buttonMedicalHistory)
        buttonDonateOrgan = findViewById(R.id.buttonDonateOrgan)
        buttonHistory = findViewById(R.id.buttonHistory)
        buttonLogout = findViewById(R.id.buttonLogout)

        val uid = auth.currentUser?.uid
        if (uid != null) {
            firestore.collection("users").document(uid).get()
                .addOnSuccessListener {
                    val name = it.getString("name")
                    welcomeText.text = "Welcome, $name"
                }
        }

        buttonMedicalHistory.setOnClickListener {
            startActivity(Intent(this, MedicalHistoryActivity::class.java))
        }

        buttonDonateOrgan.setOnClickListener {
            startActivity(Intent(this, OrganDonationActivity::class.java))
        }

        buttonHistory.setOnClickListener {
            startActivity(Intent(this, DonationHistoryActivity::class.java))
        }

        buttonLogout.setOnClickListener {
            auth.signOut()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }
}
