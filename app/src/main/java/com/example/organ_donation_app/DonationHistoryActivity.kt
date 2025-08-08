package com.example.organ_donation_app

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.organ_donation_app.adapter.DonationAdapter
import com.example.organ_donation_app.model.Donation
import com.google.firebase.firestore.FirebaseFirestore

class DonationHistoryActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var donationAdapter: DonationAdapter
    private val donationList = ArrayList<Donation>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_donation_history)

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = donationAdapter

// THIS IS WHAT ADDS SPACING — ensure it's called after setting adapter/layoutManager
        val spacingInDp = 8
        val spacingInPx = (spacingInDp * resources.displayMetrics.density).toInt()
        recyclerView.addItemDecoration(VerticalSpaceItemDecoration(24)) // 24px = 12dp approx

        fetchDonationsFromFirestore()
    }

    private fun fetchDonationsFromFirestore() {
        val db = FirebaseFirestore.getInstance()
        db.collection("donations")
            .get()
            .addOnSuccessListener { result ->
                donationList.clear()
                for (document in result) {
                    val donation = document.toObject(Donation::class.java)
                    donationList.add(donation)
                }
                donationAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener {
                // handle error
            }
    }
}
