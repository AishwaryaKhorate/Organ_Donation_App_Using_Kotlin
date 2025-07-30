package com.example.organ_donation_app

import android.os.Bundle
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

class HospitalDashboardActivity : AppCompatActivity() {

    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private lateinit var container: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hospital_dashboard)

        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()
        container = findViewById(R.id.donationListLayout)

        loadOrganDonations()
    }

    private fun loadOrganDonations() {
        firestore.collection("donations")
            .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { donationSnapshot ->
                if (donationSnapshot.isEmpty) {
                    Toast.makeText(this, "No donations found", Toast.LENGTH_SHORT).show()
                    return@addOnSuccessListener
                }

                for (doc in donationSnapshot.documents) {
                    val uid = doc.getString("uid") ?: continue
                    val organ = doc.getString("organ") ?: "Unknown"
                    val timestamp = doc.getTimestamp("timestamp") ?: Timestamp.now()
                    val dateStr = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()).format(timestamp.toDate())

                    firestore.collection("users").document(uid).get()
                        .addOnSuccessListener { userDoc ->
                            val donorName = userDoc.getString("name") ?: "Unknown Donor"

                            val entry = TextView(this).apply {
                                text = "Donor: $donorName\nOrgan: $organ\nDate: $dateStr"
                                textSize = 16f
                                setPadding(16, 16, 16, 16)
                            }

                            container.addView(entry)
                        }
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to load donations", Toast.LENGTH_SHORT).show()
            }
    }
}
