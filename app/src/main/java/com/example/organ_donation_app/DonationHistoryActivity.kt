package com.example.organ_donation_app

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.organ_donation_app.adapter.DonationAdapter
import com.example.organ_donation_app.model.Donation
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class DonationHistoryActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var donationAdapter: DonationAdapter
    private val donationList = ArrayList<Donation>()
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_donation_history)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        donationAdapter = DonationAdapter(donationList)
        recyclerView.adapter = donationAdapter

        // Keep your existing spacing
        recyclerView.addItemDecoration(VerticalSpaceItemDecoration(24))

        fetchDonationsBasedOnRole()
    }

    private fun fetchDonationsBasedOnRole() {
        val currentUser = auth.currentUser ?: return

        firestore.collection("users").document(currentUser.uid)
            .get()
            .addOnSuccessListener { document ->
                val role = document.getString("role") ?: "Donor"
                val query: Query = if (role.equals("Hospital", ignoreCase = true)) {
                    // Hospital: See all donations
                    firestore.collection("donations")
                        .orderBy("timestamp", Query.Direction.DESCENDING)
                } else {
                    // Donor: See only own donations
                    firestore.collection("donations")
                        .whereEqualTo("userId", currentUser.uid)
                        .orderBy("timestamp", Query.Direction.DESCENDING)
                }

                query.get()
                    .addOnSuccessListener { result ->
                        donationList.clear()
                        for (doc in result) {
                            val donation = doc.toObject(Donation::class.java)
                            donationList.add(donation)
                        }
                        donationAdapter.notifyDataSetChanged()
                    }
                    .addOnFailureListener {
                        // handle error
                    }
            }
            .addOnFailureListener {
                // handle error
            }
    }
}
