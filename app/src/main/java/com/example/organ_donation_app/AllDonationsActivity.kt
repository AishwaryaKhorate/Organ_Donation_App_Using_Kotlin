package com.example.organ_donation_app

import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

class AllDonationsActivity : AppCompatActivity() {

    private lateinit var firestore: FirebaseFirestore
    private lateinit var donationsLayout: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_all_donations)

        firestore = FirebaseFirestore.getInstance()
        donationsLayout = findViewById(R.id.allDonationsLayout)

        loadAllDonations()
    }

    private fun loadAllDonations() {
        firestore.collection("donations")
            .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { result ->
                if (result.isEmpty) {
                    Toast.makeText(this, "No donations found", Toast.LENGTH_SHORT).show()
                    return@addOnSuccessListener
                }

                for (doc in result.documents) {
                    val organ = doc.getString("organ") ?: "Unknown"
                    val donorUid = doc.getString("uid") ?: "N/A"
                    val timestamp = doc.getTimestamp("timestamp") ?: Timestamp.now()
                    val formattedDate = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
                        .format(timestamp.toDate())

                    val textView = TextView(this).apply {
                        text = "Organ: $organ\nDonor UID: $donorUid\nDate: $formattedDate"
                        textSize = 16f
                        setPadding(20, 20, 20, 20)
                    }

                    donationsLayout.addView(textView)
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to load donations", Toast.LENGTH_SHORT).show()
            }
    }
}
