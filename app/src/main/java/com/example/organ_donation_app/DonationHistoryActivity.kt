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

class DonationHistoryActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var donationListLayout: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_donation_history)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()
        donationListLayout = findViewById(R.id.donationHistoryList)

        loadUserDonations()
    }

    private fun loadUserDonations() {
        val uid = auth.currentUser?.uid ?: return

        firestore.collection("donations")
            .whereEqualTo("uid", uid)
            .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { result ->
                if (result.isEmpty) {
                    Toast.makeText(this, "No donation history found", Toast.LENGTH_SHORT).show()
                    return@addOnSuccessListener
                }

                for (doc in result.documents) {
                    val organ = doc.getString("organ") ?: "Unknown"
                    val timestamp = doc.getTimestamp("timestamp") ?: Timestamp.now()
                    val formattedDate = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()).format(timestamp.toDate())

                    val recordView = TextView(this).apply {
                        text = "Organ: $organ\nDate: $formattedDate"
                        textSize = 16f
                        setPadding(16, 16, 16, 16)
                    }

                    donationListLayout.addView(recordView)
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to load donation history", Toast.LENGTH_SHORT).show()
            }
    }
}
