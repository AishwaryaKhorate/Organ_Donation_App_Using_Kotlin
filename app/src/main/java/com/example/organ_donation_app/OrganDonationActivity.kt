package com.example.organ_donation_app

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class OrganDonationActivity : AppCompatActivity() {

    private lateinit var organSpinner: Spinner
    private lateinit var submitButton: Button

    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    private val organList = listOf(
        "Kidney", "Liver", "Heart", "Lungs", "Pancreas", "Intestines", "Corneas", "Skin", "Bone Marrow"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_organ_donation)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        organSpinner = findViewById(R.id.spinnerOrgan)
        submitButton = findViewById(R.id.buttonSubmitOrgan)

        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, organList)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        organSpinner.adapter = adapter

        submitButton.setOnClickListener {
            val selectedOrgan = organSpinner.selectedItem.toString()
            val uid = auth.currentUser?.uid ?: return@setOnClickListener

            val donation = hashMapOf(
                "uid" to uid,
                "organ" to selectedOrgan,
                "timestamp" to Timestamp.now(),
                "notified" to false
            )

            firestore.collection("donations").add(donation)
                .addOnSuccessListener {
                    Toast.makeText(this, "Organ donation recorded", Toast.LENGTH_SHORT).show()
                    finish()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Failed to record donation", Toast.LENGTH_SHORT).show()
                }
        }
    }
}
