package com.example.organ_donation_app

import android.graphics.Color
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.SearchView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView

import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

class AllDonationsActivity : AppCompatActivity() {

    private lateinit var donationListLayout: LinearLayout
    private lateinit var firestore: FirebaseFirestore
    private lateinit var searchView: SearchView

    private val allDonations = mutableListOf<Map<String, String>>()
    private val cardColors = listOf(
        Color.parseColor("#B3E5FC"),
        Color.parseColor("#FFB6C1"),
        Color.parseColor("#FFF9C4"),
        Color.parseColor("#C8E6C9")
    )
    private var currentCardCount = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_all_donations)

        donationListLayout = findViewById(R.id.allDonationList)
        searchView = findViewById(R.id.searchViewOrgan)
        firestore = FirebaseFirestore.getInstance()

        loadAllDonations()

        // 🔍 filter when typing
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                filterDonations(query ?: "")
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filterDonations(newText ?: "")
                return true
            }
        })
    }

    private fun loadAllDonations() {
        firestore.collection("donations")
            .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .addSnapshotListener { snapshots, e ->
                if (e != null) {
                    Toast.makeText(this, "Failed to load donations", Toast.LENGTH_SHORT).show()
                    return@addSnapshotListener
                }

                if (snapshots == null || snapshots.isEmpty) {
                    Toast.makeText(this, "No donations found", Toast.LENGTH_SHORT).show()
                    donationListLayout.removeAllViews()
                    return@addSnapshotListener
                }

                allDonations.clear()
                donationListLayout.removeAllViews()
                currentCardCount = 0

                for (doc in snapshots.documents) {
                    val organ = doc.getString("organ") ?: "Unknown"
                    val donorUid = doc.getString("userId") ?: continue
                    val timestamp = doc.getTimestamp("timestamp") ?: Timestamp.now()

                    firestore.collection("medical_history").document(donorUid)
                        .get()
                        .addOnSuccessListener { donorDoc ->
                            val name = donorDoc.getString("name") ?: "Name N/A"
                            val contact = donorDoc.getString("contact") ?: "Contact N/A"
                            val formattedDate = SimpleDateFormat(
                                "dd MMM yyyy, hh:mm a",
                                Locale.getDefault()
                            ).format(timestamp.toDate())

                            val donation = mapOf(
                                "organ" to organ,
                                "name" to name,
                                "contact" to contact,
                                "date" to formattedDate
                            )
                            allDonations.add(donation)

                            // display all by default
                            displayDonations(allDonations)
                        }
                }
            }
    }

    private fun displayDonations(list: List<Map<String, String>>) {
        donationListLayout.removeAllViews()
        currentCardCount = 0

        for (donation in list) {
            val itemView = layoutInflater.inflate(R.layout.item_donation, donationListLayout, false)

            itemView.findViewById<TextView>(R.id.textOrgan).text = "Organ: ${donation["organ"]}"
            itemView.findViewById<TextView>(R.id.textName).text = "Name: ${donation["name"]}"
            itemView.findViewById<TextView>(R.id.textContact).text = "Contact: ${donation["contact"]}"
            itemView.findViewById<TextView>(R.id.textDate).text = "Date: ${donation["date"]}"

            val imageView = itemView.findViewById<ImageView>(R.id.imageOrgan)
            imageView.setImageResource(getOrganImage(donation["organ"]!!))

            val card = itemView.findViewById<CardView>(R.id.cardViewItem)
            card.setCardBackgroundColor(cardColors[currentCardCount % cardColors.size])

            donationListLayout.addView(itemView)
            currentCardCount++
        }
    }

    private fun filterDonations(query: String) {
        val filtered = allDonations.filter {
            it["organ"]!!.contains(query, ignoreCase = true) ||
                    it["date"]!!.contains(query, ignoreCase = true)||
                        it["name"]!!.contains(query, ignoreCase = true)


        }
        displayDonations(filtered)
    }


    private fun getOrganImage(organ: String): Int {
        return when (organ.lowercase(Locale.getDefault())) {
            "kidney" -> R.drawable.kidney
            "liver" -> R.drawable.liver
            "heart" -> R.drawable.heart
            "lungs" -> R.drawable.lungs
            "pancreas" -> R.drawable.pancreas
            "intestine" -> R.drawable.intestine
            "eye" -> R.drawable.eye
            "skin" -> R.drawable.skin
            "bonemarrow" -> R.drawable.bone_marrow
            else -> R.drawable.ic_organ
        }
    }
}
