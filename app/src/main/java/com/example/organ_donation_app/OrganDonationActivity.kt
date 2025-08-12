package com.example.organ_donation_app

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class OrganDonationActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: OrganAdapter
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    private val organList = listOf(
        "Kidney", "Liver", "Heart", "Lungs", "Pancreas", "Intestines", "Corneas", "Skin", "Bone Marrow"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_organ_donation) // Keep your existing UI

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        recyclerView = findViewById(R.id.recyclerOrgans) // Make sure this matches your XML RecyclerView ID
        recyclerView.layoutManager = GridLayoutManager(this, 2) // Keep your grid style

        adapter = OrganAdapter(organList) { selectedOrgan ->
            saveDonation(selectedOrgan) // Called when donor clicks an organ card
        }
        recyclerView.adapter = adapter
    }

    private fun saveDonation(selectedOrgan: String) {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            val donationData = hashMapOf(
                "userId" to currentUser.uid,
                "organ" to selectedOrgan,
                "timestamp" to Timestamp.now() // For newest-first ordering
            )

            firestore.collection("donations")
                .add(donationData)
                .addOnSuccessListener {
                    Toast.makeText(this, "$selectedOrgan donation recorded", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
        }
    }
}
