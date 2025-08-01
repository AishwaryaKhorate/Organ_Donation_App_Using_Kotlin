package com.example.organ_donation_app

import android.os.Bundle
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

class AllMedicalHistoryActivity : AppCompatActivity() {

    private lateinit var firestore: FirebaseFirestore
    private lateinit var medicalListLayout: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_all_medical_history)

        firestore = FirebaseFirestore.getInstance()
        medicalListLayout = findViewById(R.id.medicalHistoryList)

        loadAllMedicalHistories()
    }

    private fun loadAllMedicalHistories() {
        firestore.collection("medical_history")
            .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { result ->
                if (result.isEmpty) {
                    Toast.makeText(this, "No medical history found", Toast.LENGTH_SHORT).show()
                    return@addOnSuccessListener
                }

                for (doc in result.documents) {
                    val name = doc.getString("name") ?: "N/A"
                    val contact = doc.getString("contact") ?: "N/A"
                    val address = doc.getString("address") ?: "N/A"
                    val bloodGroup = doc.getString("bloodGroup") ?: "N/A"
                    val diseases = doc.getString("diseases") ?: "N/A"
                    val medications = doc.getString("medications") ?: "N/A"
                    val allergies = doc.getString("allergies") ?: "N/A"
                    val smoking = doc.getString("smoking") ?: "N/A"
                    val height = doc.getString("height") ?: "N/A"
                    val weight = doc.getString("weight") ?: "N/A"
                    val surgeries = doc.getString("surgeries") ?: "N/A"
                    val timestamp = doc.getTimestamp("timestamp") ?: Timestamp.now()
                    val dateFormatted = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
                        .format(timestamp.toDate())

                    val info = """
                        Name: $name
                        Contact: $contact
                        Address: $address
                        Blood Group: $bloodGroup
                        Diseases: $diseases
                        Medications: $medications
                        Allergies: $allergies
                        Smoking: $smoking
                        Height: $height cm
                        Weight: $weight kg
                        Surgeries: $surgeries
                        Date: $dateFormatted
                    """.trimIndent()

                    val textView = TextView(this).apply {
                        text = info
                        textSize = 15f
                        setPadding(24, 24, 24, 24)
                    }

                    medicalListLayout.addView(textView)
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error loading medical histories", Toast.LENGTH_SHORT).show()
            }
    }
}
