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

    private var isSubmitting = false // Prevent multiple submissions

    private val organList = listOf(
        "Kidney", "Liver", "Heart", "Lungs", "Pancreas", "Intestines", "Corneas", "Skin", "Bone Marrow"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_organ_donation)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        recyclerView = findViewById(R.id.recyclerOrgans)
        recyclerView.layoutManager = GridLayoutManager(this, 2)

        adapter = OrganAdapter(organList) { selectedOrgan ->
            checkMedicalFormAndDonate(selectedOrgan)
        }
        recyclerView.adapter = adapter
    }

    private fun checkMedicalFormAndDonate(selectedOrgan: String) {
        val currentUser = auth.currentUser
        if (currentUser == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
            return
        }

        firestore.collection("users")
            .document(currentUser.uid)
            .get()
            .addOnSuccessListener { document ->
                val medicalFormCompleted = document.getBoolean("medicalFormCompleted") ?: false
                if (!medicalFormCompleted) {
                    Toast.makeText(this, "Please complete your medical history form first.", Toast.LENGTH_LONG).show()
                } else {
                    saveDonation(selectedOrgan)
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to verify medical form status", Toast.LENGTH_SHORT).show()
            }
    }

    private fun saveDonation(selectedOrgan: String) {
        if (isSubmitting) return
        isSubmitting = true

        val currentUser = auth.currentUser ?: return

        val donationData = hashMapOf(
            "userId" to currentUser.uid,
            "organ" to selectedOrgan,
            "timestamp" to Timestamp.now() // Accurate submission time
        )

        firestore.collection("donations")
            .add(donationData)
            .addOnSuccessListener {
                Toast.makeText(this, "$selectedOrgan donation recorded", Toast.LENGTH_SHORT).show()
                isSubmitting = false
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                isSubmitting = false
            }
    }
}
