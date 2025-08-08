package com.example.organ_donation_app

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class OrganDonationActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    private val organList = listOf(
        "Kidney", "Liver", "Heart", "Lungs", "Pancreas",
        "Intestines", "Corneas", "Skin", "Bone Marrow"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_organ_donation) // Make sure this layout contains RecyclerView with ID recyclerOrgans

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerOrgans)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val adapter = OrganAdapter(organList) { selectedOrgan ->
            val uid = auth.currentUser?.uid ?: return@OrganAdapter

            firestore.collection("medical_history").document(uid).get()
                .addOnSuccessListener { document ->
                    if (!document.exists()) {
                        Toast.makeText(this, "Please fill medical history before donating", Toast.LENGTH_LONG).show()
                        return@addOnSuccessListener
                    }

                    val donation = hashMapOf(
                        "uid" to uid,
                        "organ" to selectedOrgan,
                        "timestamp" to Timestamp.now(),
                        "notified" to false
                    )

                    firestore.collection("donations").add(donation)
                        .addOnSuccessListener {
                            Toast.makeText(this, "$selectedOrgan donation recorded", Toast.LENGTH_SHORT).show()
                            finish()
                        }
                        .addOnFailureListener {
                            Toast.makeText(this, "Failed to record donation", Toast.LENGTH_SHORT).show()
                        }
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Error checking medical history", Toast.LENGTH_SHORT).show()
                }
        }

        recyclerView.adapter = adapter
    }
}
