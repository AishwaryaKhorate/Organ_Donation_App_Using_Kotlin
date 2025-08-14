package com.example.organ_donation_app

import android.graphics.Color
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
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

    // pastel colors (will cycle)
    private val cardColors = listOf(
        Color.parseColor("#B3E5FC"), // Light Blue
        Color.parseColor("#FFB6C1"), // Light Pink
        Color.parseColor("#FFF9C4"), // Light Yellow
        Color.parseColor("#C8E6C9")  // Light Green
    )

    private var currentCardCount = 0
    private val displayedDonationIds = mutableSetOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_all_donations)

        donationListLayout = findViewById(R.id.allDonationList)
        firestore = FirebaseFirestore.getInstance()

        loadAllDonations()
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
                    displayedDonationIds.clear()
                    return@addSnapshotListener
                }

                // Clear and rebuild list
                donationListLayout.removeAllViews()
                displayedDonationIds.clear()
                currentCardCount = 0

                for (doc in snapshots.documents) {
                    val donationId = doc.id
                    displayedDonationIds.add(donationId)

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

                            val itemView = layoutInflater.inflate(
                                R.layout.item_donation,
                                donationListLayout,
                                false
                            )

                            // Set text values
                            itemView.findViewById<TextView>(R.id.textOrgan).text = "Organ: $organ"
                            itemView.findViewById<TextView>(R.id.textName).text = "Name: $name"
                            itemView.findViewById<TextView>(R.id.textContact).text = "Contact: $contact"
                            itemView.findViewById<TextView>(R.id.textDate).text = "Date: $formattedDate"

                            // Set organ image
                            val imageView = itemView.findViewById<ImageView>(R.id.imageOrgan)
                            imageView.setImageResource(getOrganImage(organ))

                            // Set card appearance
                            val card = itemView.findViewById<CardView>(R.id.cardViewItem)
                            card.setCardBackgroundColor(cardColors[currentCardCount % cardColors.size])
                            card.radius = 12f
                            card.cardElevation = 6f

                            donationListLayout.addView(itemView) // Already ordered by query
                            currentCardCount++
                        }
                        .addOnFailureListener {
                            Toast.makeText(
                                this,
                                "Error fetching donor info",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                }
            }
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
