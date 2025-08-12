package com.example.organ_donation_app

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.organ_donation_app.adapter.DonationAdapter
import com.example.organ_donation_app.model.Donation
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class DonationHistoryActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var donationAdapter: DonationAdapter
    private val donationList = ArrayList<Donation>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_donation_history)

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        donationAdapter = DonationAdapter(donationList)
        recyclerView.adapter = donationAdapter

        // Keep your existing spacing
        recyclerView.addItemDecoration(VerticalSpaceItemDecoration(24))

        fetchDonationsFromFirestore()
    }

    private fun fetchDonationsFromFirestore() {
        val db = FirebaseFirestore.getInstance()
        db.collection("donations")
            .orderBy("timestamp", Query.Direction.DESCENDING) // 🔹 Newest first
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
