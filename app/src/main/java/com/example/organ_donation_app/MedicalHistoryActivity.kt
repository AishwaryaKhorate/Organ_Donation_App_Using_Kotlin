package com.example.organ_donation_app

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MedicalHistoryActivity : AppCompatActivity() {

    private lateinit var editBloodGroup: EditText
    private lateinit var editDiseases: EditText
    private lateinit var editMedications: EditText
    private lateinit var editAllergies: EditText
    private lateinit var buttonSubmit: Button

    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_medical_history)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        editBloodGroup = findViewById(R.id.editTextBloodGroup)
        editDiseases = findViewById(R.id.editTextDiseases)
        editMedications = findViewById(R.id.editTextMedications)
        editAllergies = findViewById(R.id.editTextAllergies)
        buttonSubmit = findViewById(R.id.buttonSubmitMedical)

        buttonSubmit.setOnClickListener {
            val bloodGroup = editBloodGroup.text.toString().trim()
            val diseases = editDiseases.text.toString().trim()
            val medications = editMedications.text.toString().trim()
            val allergies = editAllergies.text.toString().trim()

            if (bloodGroup.isEmpty()) {
                Toast.makeText(this, "Blood group is required", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val uid = auth.currentUser?.uid ?: return@setOnClickListener
            val historyData = hashMapOf(
                "uid" to uid,
                "bloodGroup" to bloodGroup,
                "diseases" to diseases,
                "medications" to medications,
                "allergies" to allergies,
                "timestamp" to Timestamp.now()
            )

            firestore.collection("medical_history").document(uid)
                .set(historyData)
                .addOnSuccessListener {
                    Toast.makeText(this, "Medical history saved", Toast.LENGTH_SHORT).show()
                    finish()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Failed to save data", Toast.LENGTH_SHORT).show()
                }
        }
    }
}
