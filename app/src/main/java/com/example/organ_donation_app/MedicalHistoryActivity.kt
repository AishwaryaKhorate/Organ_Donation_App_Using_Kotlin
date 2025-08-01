package com.example.organ_donation_app

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MedicalHistoryActivity : AppCompatActivity() {

    private lateinit var editName: EditText
    private lateinit var editContact: EditText
    private lateinit var editAddress: EditText
    private lateinit var editBloodGroup: EditText
    private lateinit var editDiseases: EditText
    private lateinit var editMedications: EditText
    private lateinit var editAllergies: EditText
    private lateinit var editSmoking: EditText
    private lateinit var editHeight: EditText
    private lateinit var editWeight: EditText
    private lateinit var editSurgeries: EditText
    private lateinit var buttonSubmit: Button

    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_medical_history)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        // Initialize fields
        editName = findViewById(R.id.editTextName)
        editContact = findViewById(R.id.editTextContact)
        editAddress = findViewById(R.id.editTextAddress)
        editBloodGroup = findViewById(R.id.editTextBloodGroup)
        editDiseases = findViewById(R.id.editTextDiseases)
        editMedications = findViewById(R.id.editTextMedications)
        editAllergies = findViewById(R.id.editTextAllergies)
        editSmoking = findViewById(R.id.editTextSmoking)
        editHeight = findViewById(R.id.editTextHeight)
        editWeight = findViewById(R.id.editTextWeight)
        editSurgeries = findViewById(R.id.editTextSurgeries)
        buttonSubmit = findViewById(R.id.buttonSubmitMedical)

        buttonSubmit.setOnClickListener {
            val name = editName.text.toString().trim()
            val contact = editContact.text.toString().trim()
            val address = editAddress.text.toString().trim()
            val bloodGroup = editBloodGroup.text.toString().trim()
            val diseases = editDiseases.text.toString().trim()
            val medications = editMedications.text.toString().trim()
            val allergies = editAllergies.text.toString().trim()
            val smoking = editSmoking.text.toString().trim()
            val height = editHeight.text.toString().trim()
            val weight = editWeight.text.toString().trim()
            val surgeries = editSurgeries.text.toString().trim()

            if (name.isEmpty() || contact.isEmpty() || address.isEmpty() || bloodGroup.isEmpty()) {
                Toast.makeText(this, "Please fill all required fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val uid = auth.currentUser?.uid ?: return@setOnClickListener
            val data = hashMapOf(
                "uid" to uid,
                "name" to name,
                "contact" to contact,
                "address" to address,
                "bloodGroup" to bloodGroup,
                "diseases" to diseases,
                "medications" to medications,
                "allergies" to allergies,
                "smoking" to smoking,
                "height" to height,
                "weight" to weight,
                "surgeries" to surgeries,
                "timestamp" to Timestamp.now()
            )

            firestore.collection("medical_history").document(uid)
                .set(data)
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
