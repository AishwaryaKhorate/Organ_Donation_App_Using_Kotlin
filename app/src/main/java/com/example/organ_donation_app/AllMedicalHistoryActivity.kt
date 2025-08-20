package com.example.organ_donation_app

import android.graphics.Color
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

class AllMedicalHistoryActivity : AppCompatActivity() {

    private lateinit var historyListLayout: LinearLayout
    private lateinit var firestore: FirebaseFirestore
    private lateinit var searchView: SearchView

    private val donorList = mutableListOf<Map<String, String>>()
    private val cardColors = listOf(
        Color.parseColor("#B3E5FC"), // Light Blue
        Color.parseColor("#FFB6C1"), // Light Pink
        Color.parseColor("#FFF9C4"), // Light Yellow
        Color.parseColor("#C8E6C9")  // Light Green
    )
    private var currentCardCount = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_all_medical_history)

        historyListLayout = findViewById(R.id.allMedicalHistoryList)
        searchView = findViewById(R.id.searchViewDonor)
        firestore = FirebaseFirestore.getInstance()

        loadAllMedicalHistories()

        // Listen to search input
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                filterDonors(query ?: "")
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filterDonors(newText ?: "")
                return true
            }
        })
    }

    private fun loadAllMedicalHistories() {
        firestore.collection("medical_history")
            .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .addSnapshotListener { snapshots, e ->
                if (e != null) {
                    Toast.makeText(this, "Failed to load medical records", Toast.LENGTH_SHORT).show()
                    return@addSnapshotListener
                }

                if (snapshots == null || snapshots.isEmpty) {
                    Toast.makeText(this, "No medical records found", Toast.LENGTH_SHORT).show()
                    historyListLayout.removeAllViews()
                    return@addSnapshotListener
                }

                donorList.clear()
                historyListLayout.removeAllViews()
                currentCardCount = 0

                for (doc in snapshots.documents) {
                    val donor = mapOf(
                        "name" to (doc.getString("name") ?: "N/A"),
                        "contact" to (doc.getString("contact") ?: "N/A"),
                        "address" to (doc.getString("address") ?: "N/A"),
                        "bloodGroup" to (doc.getString("bloodGroup") ?: "N/A"),
                        "diseases" to (doc.getString("diseases") ?: "N/A"),
                        "medications" to (doc.getString("medications") ?: "N/A"),
                        "allergies" to (doc.getString("allergies") ?: "N/A"),
                        "date" to SimpleDateFormat(
                            "dd MMM yyyy, hh:mm a",
                            Locale.getDefault()
                        ).format((doc.getTimestamp("timestamp") ?: Timestamp.now()).toDate())
                    )
                    donorList.add(donor)
                }
                displayDonors(donorList)
            }
    }

    private fun displayDonors(list: List<Map<String, String>>) {
        historyListLayout.removeAllViews()
        currentCardCount = 0

        for (donor in list) {
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
                    Name: ${donor["name"]}
                    Contact: ${donor["contact"]}
                    Address: ${donor["address"]}
                    Blood Group: ${donor["bloodGroup"]}
                    Diseases: ${donor["diseases"]}
                    Medications: ${donor["medications"]}
                    Allergies: ${donor["allergies"]}
                    Date: ${donor["date"]}
                """.trimIndent()
                textSize = 15f
                setTextColor(Color.BLACK)
            }

            card.addView(tv)
            historyListLayout.addView(card)
            currentCardCount++
        }
    }

    private fun filterDonors(query: String) {
        val filtered = donorList.filter {
            it["address"]!!.contains(query, true)||
                it["date"]!!.contains(query, true)||
                     it["name"]!!.contains(query, true)||
                             it["bloodGroup"]!!.contains(query, true)


        }
        displayDonors(filtered)
    }
}

