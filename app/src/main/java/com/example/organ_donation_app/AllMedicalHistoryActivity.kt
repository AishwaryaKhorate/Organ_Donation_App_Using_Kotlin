package com.example.organ_donation_app

import android.graphics.Color
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

class AllMedicalHistoryActivity : AppCompatActivity() {

    private lateinit var historyListLayout: LinearLayout
    private lateinit var firestore: FirebaseFirestore

    private val cardColors = listOf(
        Color.parseColor("#B3E5FC"), // Light Blue
        Color.parseColor("#FFB6C1"), // Light Pink
        Color.parseColor("#FFF9C4"), // Light Yellow
        Color.parseColor("#C8E6C9")  // Light Green
    )

    private var currentCardCount = 0
    private val displayedHistoryIds = mutableSetOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_all_medical_history)

        historyListLayout = findViewById(R.id.allMedicalHistoryList)
        firestore = FirebaseFirestore.getInstance()

        loadAllMedicalHistories()
    }

    private fun loadAllMedicalHistories() {
        firestore.collection("medical_history")
            .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { result ->
                if (result.isEmpty) {
                    Toast.makeText(this, "No medical records found", Toast.LENGTH_SHORT).show()
                    return@addOnSuccessListener
                }

                for (doc in result.documents) {
                    val histId = doc.id
                    if (displayedHistoryIds.contains(histId)) continue
                    displayedHistoryIds.add(histId)

                    val name = doc.getString("name") ?: "N/A"
                    val contact = doc.getString("contact") ?: "N/A"
                    val address = doc.getString("address") ?: "N/A"
                    val bloodGroup = doc.getString("bloodGroup") ?: "N/A"
                    val diseases = doc.getString("diseases") ?: "N/A"
                    val medications = doc.getString("medications") ?: "N/A"
                    val allergies = doc.getString("allergies") ?: "N/A"
                    val timestamp = doc.getTimestamp("timestamp") ?: Timestamp.now()
                    val formattedDate = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
                        .format(timestamp.toDate())

                    // Build view programmatically (CardView)
                    val card = CardView(this).apply {
                        radius = 16f
                        cardElevation = 6f
                        setContentPadding(24, 24, 24, 24)
                        setCardBackgroundColor(cardColors[currentCardCount % cardColors.size])
                        val params = LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                        )
                        params.setMargins(0, 0, 0, 20)
                        layoutParams = params
                    }

                    val tv = TextView(this).apply {
                        text = """
                            Name: $name
                            Contact: $contact
                            Address: $address
                            Blood Group: $bloodGroup
                            Diseases: $diseases
                            Medications: $medications
                            Allergies: $allergies
                            Date: $formattedDate
                        """.trimIndent()
                        textSize = 15f
                        setTextColor(Color.BLACK)
                    }

                    card.addView(tv)

                    // add at top
                    historyListLayout.addView(card, 0)
                    currentCardCount++
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to load medical records", Toast.LENGTH_SHORT).show()
            }
    }
}
